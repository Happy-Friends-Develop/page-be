package com.example.hello_friends.auth.application;

import com.example.hello_friends.auth.domain.Auth;
import com.example.hello_friends.auth.domain.AuthRepository;
import com.example.hello_friends.auth.domain.DuplicateLoginId;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor // 생성자 자동 호출
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final AuthRepository authRepository;

    // 새로운 사용자 인증 정보 등록
    @Transactional
    public Auth registAuth(AuthBody authBody){
        // ID 중복체크
        Optional<Auth> isExist = authRepository.findByLoginId(authBody.getLoginId());
        // 중복 예외처리
        if(!isExist.isEmpty())
            throw new DuplicateLoginId();
        // 인증 정보 저장하고 비밀번호는 암호화 후 저장
        return authRepository.save(new Auth(authBody.getLoginId(), passwordEncoder.encode(authBody.getPwd())));
    }
}