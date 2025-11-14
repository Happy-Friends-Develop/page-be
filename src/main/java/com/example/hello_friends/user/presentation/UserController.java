package com.example.hello_friends.user.presentation;

import com.example.hello_friends.common.response.Resp;
import com.example.hello_friends.user.application.request.*;
import com.example.hello_friends.user.application.response.UserResponse;
import com.example.hello_friends.user.application.service.BlackUserService;
import com.example.hello_friends.user.application.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Resp<UserResponse>> register(@RequestBody @Valid UserRequest userRequest) {
        return ResponseEntity.ok(Resp.ok(userService.register(userRequest)));
    }


    @Operation(summary = "사용자 정보 조회", description = "마이페이지에서 로그인한 회원의 정보를 확인합니다.")
    @GetMapping("/api/user/{id}")
    public ResponseEntity<Resp<UserResponse>> getUserInfo(@PathVariable Long id) {
        return ResponseEntity.ok(Resp.ok(userService.findUserInformation(id)));
    }

    @Operation(summary = "유저들 정보 조회", description = "회원들의 정보를 확인합니다.")
    @GetMapping("/api/admin/users")
    public ResponseEntity<Resp<List<UserResponse>>> getUsersInfo() {
        return ResponseEntity.ok(Resp.ok(userService.findUsersInformation()));
    }

    @Operation(summary = "유저 정보 수정", description = "회원의 정보를 변경합니다.")
    @PutMapping("/api/user/{id}")
    public ResponseEntity<Resp<UserResponse>> updateUserInfo(@PathVariable Long id, @RequestBody UserUpdateRequest userUpdateRequest) {
        return ResponseEntity.ok(Resp.ok(userService.updateUserInformation(id, userUpdateRequest)));
    }

    @Operation(summary = "유저 탈퇴", description = "회원이 탈퇴합니다.")
    @DeleteMapping("/api/user/{id}")
    public ResponseEntity<Resp<String>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(Resp.ok("회원 정보가 삭제되었습니다."));
    }

    @Operation(summary = "사용자 블랙리스트 등록", description = "특정 사용자를 블랙리스트로 지정하고 상태를 변경")
    @PostMapping("/api/admin/users/{userId}/blacklist")
    public ResponseEntity<Resp<UserResponse>> blacklistUser(
            @PathVariable Long userId,
            @RequestBody BlacklistRequest request
    ) {
        UserResponse blacklistedUser = userService.blackListUser(userId, request.reason(), request.endDate());
        return ResponseEntity.ok(Resp.ok(blacklistedUser));
    }

    @Operation(summary = "사용자 블랙리스트 해제", description = "블랙리스트에 등록된 사용자를 다시 활성 상태로 변경")
    @DeleteMapping("/api/admin/users/{userId}/blacklist")
    public ResponseEntity<Resp<UserResponse>> unblacklistUser(
            @PathVariable Long userId,
            @RequestBody UnblacklistRequest request
    ) {
        UserResponse unblacklistedUser = userService.unblackListUser(userId, request.memo());
        return ResponseEntity.ok(Resp.ok(unblacklistedUser));
    }

    @Operation(summary = "사용자에게 경고 부여", description = "경고를 부여하고 3회 누적 시 블랙리스트 처리")
    @PostMapping("/api/admin/users/{userId}/warning")
    public ResponseEntity<Resp<String>> giveWarningToUser(
            @PathVariable Long userId,
            @RequestBody WarnUserRequest request
    ) {
        blackUserService.addWarningAndBlacklistIfNeeded(
                userId,
                request.reason(), // 첫 번째: 경고 사유 (reason)
                request.reason()  // 두 번째: 관리자 메모 (adminMemo)
        );
        return ResponseEntity.ok(Resp.ok("경고가 정상적으로 처리되었습니다"));
    }
}
