package com.example.hello_friends.user.application.service;

import com.example.hello_friends.auth.application.AuthBody;
import com.example.hello_friends.auth.application.AuthService;
import com.example.hello_friends.auth.domain.Auth;
import com.example.hello_friends.auth.domain.AuthRepository;
import com.example.hello_friends.board.domain.BoardLikeRepository;
import com.example.hello_friends.board.domain.BoardRepository;
import com.example.hello_friends.common.entity.EntityState;
import com.example.hello_friends.common.exception.UserNotFoundException;
import com.example.hello_friends.common.response.MotherException;
import com.example.hello_friends.user.application.request.UserRequest;
import com.example.hello_friends.user.application.request.UserUpdateRequest;
import com.example.hello_friends.user.application.response.UserResponse;
import com.example.hello_friends.user.domain.BlackUser;
import com.example.hello_friends.user.domain.BlackUserRepository;
import com.example.hello_friends.user.domain.User;
import com.example.hello_friends.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AuthService authService;
    private final BlackUserRepository blackUserRepository;
    private final BlackUserService blackUserService;
    private final BoardLikeRepository boardLikeRepository;
    private final BoardRepository boardRepository;
    private final AuthRepository authRepository;

    @Transactional
    public UserResponse register(UserRequest userRequest) {
        if (userRequest.getId() == null) {
            throw new UserNotFoundException("로그인 ID가 존재하지 않음");
        }
        blackUserService.validateUserRegistration(userRequest.getEmail(), userRequest.getPhone());

        Auth auth = authService.registAuth(AuthBody.of(userRequest.getId(), userRequest.getPassword()));
        User user = new User(userRequest.getName(), userRequest.getNickname(), userRequest.getPhone(),
                userRequest.getEmail(), userRequest.getAddress(), auth.getId(), userRequest.getBirth());
        User savedUser = userRepository.save(user);

        return UserResponse.from(savedUser);
    }

    // 마이페이지 -> 사용자 정보 확인
    @Transactional(readOnly = true)
    public UserResponse findMyInformation(Long id) {
        User user = userRepository.findByIdAndState(id, EntityState.ACTIVE)
                .orElseThrow(() -> new UserNotFoundException("해당하는 사용자를 찾을 수 없습니다. ID : " + id));
        // 엔티티를 DTO로 변환하여 반환
        return UserResponse.from(user);
    }

    // 관리자용 사용자 정보 확인
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public UserResponse findUserInformation(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("해당하는 사용자를 찾을 수 없습니다. ID : " + id));
        return UserResponse.from(user);
    }

    // 관리자 전용, 사용자들 정보 확인(List)
    @Transactional(readOnly = true)
    public List<UserResponse> findUsersInformation() {
        List<User> users = userRepository.findAllByState(EntityState.ACTIVE);
        // 엔티티 리스트를 DTO 리스트로 변환하여 반환
        return users.stream()
                .map(UserResponse::from)
                .toList();
    }

    // 사용자 정보 수정
    @Transactional
    public UserResponse updateUserInformation(Long id, UserUpdateRequest userUpdateRequest) {
        User user = userRepository.findByIdAndState(id, EntityState.ACTIVE)
                .orElseThrow(() -> new UserNotFoundException("해당하는 사용자를 찾을 수 없습니다. ID : " + id));

        user.update(userUpdateRequest.getName(), userUpdateRequest.getNickname(), userUpdateRequest.getPhone(), userUpdateRequest.getEmail(), userUpdateRequest.getAddress(), userUpdateRequest.getBirth());

        // 수정된 정보를 담은 엔티티를 DTO로 변환하여 반환
        return UserResponse.from(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다. ID : " + userId));
        Long authId = user.getAuthId();
        userRepository.delete(user);
        authRepository.deleteById(authId);
    }

    // 블랙리스트 추가
    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public UserResponse blackListUser(Long id, String reason, LocalDateTime endDate) {
        User user = userRepository.findByIdAndState(id, EntityState.ACTIVE)
                .orElseThrow(() -> new UserNotFoundException("해당하는 사용자를 찾을 수 없습니다. ID : " + id));

        if (blackUserRepository.existsByUserAndState(user, EntityState.ACTIVE)) {
            BlackUser existingBlackUser = blackUserRepository.findByUserAndState(user, EntityState.ACTIVE)
                    .orElseThrow();
            existingBlackUser.updateBlackEndDate(endDate);
            existingBlackUser.addAdminMemo("경고 횟수 증가 및 제재기간 연장: " + reason);
        } else {
            user.delete();
            BlackUser blackUser = new BlackUser(user, reason, endDate);
            blackUserRepository.save(blackUser);
        }
        return UserResponse.from(user);
    }

    // 블랙리스트 해제
    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public UserResponse unblackListUser(Long id, String memo) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("해당하는 사용자를 찾을 수 없습니다. ID : " + id));

        BlackUser blackUser = blackUserRepository.findByUserAndState(user, EntityState.ACTIVE)
                .orElseThrow(() -> new MotherException("블랙리스트에 등록되지 않은 사용자입니다.", HttpStatus.BAD_REQUEST));

        blackUser.deactivate(memo);
        user.activate();

        return UserResponse.from(user);
    }
}