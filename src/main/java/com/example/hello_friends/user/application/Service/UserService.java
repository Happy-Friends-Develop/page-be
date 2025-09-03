package com.example.hello_friends.user.application.Service;

import com.example.hello_friends.auth.application.AuthBody;
import com.example.hello_friends.auth.application.AuthService;
import com.example.hello_friends.auth.domain.Auth;
import com.example.hello_friends.common.entity.EntityState;
import com.example.hello_friends.user.application.Request.UserRequest;
import com.example.hello_friends.user.application.Request.UserUpdateRequest;
import com.example.hello_friends.user.domain.User;
import com.example.hello_friends.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AuthService authService;

    @Transactional
    public User register(UserRequest userRequest) {
        try {
            if (userRequest.getId() == null) {
                throw new IllegalArgumentException("로그인 ID가 존재하지 않음");
            }
            Auth auth = authService.registAuth(AuthBody.of(userRequest.getId(), userRequest.getPassword()));
            User user = new User(userRequest.getName(), userRequest.getNickname(), userRequest.getPhone(), userRequest.getEmail(), userRequest.getAddress(), auth.getId());
            userRepository.save(user);
            return user;
        } catch (Exception e) {
            log.error("회원가입 실패: {}", e.getMessage());
            throw new RuntimeException("회원가입 처리 중 오류 발생", e);
        }
    }

    // 마이페이지 -> 사용자 정보 확인
    @Transactional(readOnly = true)
    public User findUserInformation(Long id) {
        try {
            return userRepository.findByIdAndState(id, EntityState.ACTIVE)
                    .orElseThrow(() -> new IllegalArgumentException("ID " + id + "에 해당하는 사용자를 찾을 수 없음"));
        } catch (IllegalArgumentException e) {
            log.warn(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("사용자 정보 조회 실패: id={}", id, e);
            throw new RuntimeException("사용자 정보 조회 중 오류 발생", e);
        }
    }

    // 관리자 전용, 사용자들 정보 확인(List)
    @Transactional(readOnly = true)
    public List<User> findUsersInformation() {
        try {
            return userRepository.findAllByState(EntityState.ACTIVE);
        } catch (Exception e) {
            log.error("전체 사용자 정보 조회 실패", e);
            throw new RuntimeException("전체 사용자 정보 조회 중 오류 발생", e);
        }
    }

    // 사용자 정보 수정
    @Transactional
    public User updateUserInformation(Long id, UserUpdateRequest userUpdateRequest) {
        try {
            User user = userRepository.findByIdAndState(id, EntityState.ACTIVE)
                    .orElseThrow(() -> new IllegalArgumentException("ID " + id + "에 해당하는 사용자를 찾을 수 없어 수정 불가"));

            user.update(userUpdateRequest.getName(), userUpdateRequest.getNickname(), userUpdateRequest.getPhone(), userUpdateRequest.getEmail(), userUpdateRequest.getAddress());

            return user;
        } catch (IllegalArgumentException e) {
            log.warn(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("사용자 정보 수정 실패: id={}", id, e);
            throw new RuntimeException("사용자 정보 수정 중 오류 발생", e);
        }
    }

    // 사용자 탈퇴
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // 사용자 블랙리스트
    public User blackListUser(Long id) {
        // 기존 사용자 Stata를 DELETE로 변경하고
        // id를 블랙리스트 테이블에 추가
        // 블랙리스트 테이블에 있는 계정은 회원가입 및 로그인 불가
        return null;
    }
}