package com.example.hello_friends.user.application;

import com.example.hello_friends.auth.application.AuthBody;
import com.example.hello_friends.auth.application.AuthService;
import com.example.hello_friends.auth.domain.Auth;
import com.example.hello_friends.user.domain.User;
import com.example.hello_friends.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AuthService authService;

    @Transactional
    public User register(UserRequest userRequest){
        if(userRequest.getId() == null){
            throw new IllegalArgumentException("로그인 ID가 존재하지 않습니다.");
        }
        Auth auth = authService.registAuth(AuthBody.of(userRequest.getId(), userRequest.getPassword()));
        User user = new User(userRequest.getName(), userRequest.getNickname(), userRequest.getPhone(), userRequest.getEmail(), userRequest.getAddress(), auth.getId());
        userRepository.save(user);
        return user;
    }
}
