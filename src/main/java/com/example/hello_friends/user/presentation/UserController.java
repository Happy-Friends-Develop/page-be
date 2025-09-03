package com.example.hello_friends.user.presentation;

import com.example.hello_friends.common.response.Resp;
import com.example.hello_friends.user.application.Request.*;
import com.example.hello_friends.user.application.Service.BlackUserService;
import com.example.hello_friends.user.application.Service.UserService;
import com.example.hello_friends.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@Tag(name = "사용자")
public class UserController {
    private final UserService userService;
    private final BlackUserService blackUserService;

    @Operation(summary = "회원가입", description = "회원가입합니다.")
    @PostMapping("/api/user/register")
    public User register(@RequestBody @Valid UserRequest userRequest) {
        return userService.register(userRequest);
    }


    @Operation(summary = "사용자 정보 조회", description = "마이페이지에서 로그인한 회원의 정보를 확인합니다.")
    @GetMapping("/api/user/{id}")
    public User getUserInfo(@PathVariable Long id) {
        return userService.findUserInformation(id);
    }

    @Operation(summary = "유저들 정보 조회", description = "회원들의 정보를 확인합니다.")
    @GetMapping("/api/admin/users")
    public Resp<List<User>> getUsersInfo() {
        return Resp.ok(userService.findUsersInformation());
    }

    @Operation(summary = "유저 정보 수정", description = "회원의 정보를 변경합니다.")
    @PutMapping("/api/user/{id}")
    public User updateUserInfo(@PathVariable Long id, @RequestBody UserUpdateRequest userUpdateRequest) {
        return userService.updateUserInformation(id, userUpdateRequest);
    }

    @Operation(summary = "유저 탈퇴", description = "회원이 탈퇴합니다.")
    @DeleteMapping("/api/user/{id}")
    public Resp<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Resp.ok("회원 정보가 삭제되었습니다.");
    }

    @Operation(summary = "사용자 블랙리스트 등록", description = "특정 사용자를 블랙리스트로 지정하고 상태를 변경")
    @PostMapping("/api/admin/users/{userId}/blacklist")
    public Resp<User> blacklistUser(
            @PathVariable Long userId,
            @RequestBody BlacklistRequest request
    ) {
        User blacklistedUser = userService.blackListUser(userId, request.reason(), request.endDate());
        return Resp.ok(blacklistedUser);
    }

    @Operation(summary = "사용자 블랙리스트 해제", description = "블랙리스트에 등록된 사용자를 다시 활성 상태로 변경")
    @DeleteMapping("/api/admin/users/{userId}/blacklist")
    public Resp<User> unblacklistUser(
            @PathVariable Long userId,
            @RequestBody UnblacklistRequest request
    ) {
        User unblacklistedUser = userService.unblackListUser(userId, request.memo());
        return Resp.ok(unblacklistedUser);
    }

    @Operation(summary = "사용자에게 경고 부여", description = "경고를 부여하고 3회 누적 시 블랙리스트 처리")
    @PostMapping("/api/admin/users/{userId}/warning")
    public Resp<String> giveWarningToUser(
            @PathVariable Long userId,
            @RequestBody WarnUserRequest request
    ) {
        blackUserService.addWarningAndBlacklistIfNeeded(userId, request.reason());
        return Resp.ok("경고가 정상적으로 처리되었습니다");
    }
}
