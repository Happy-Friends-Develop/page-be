package com.example.hello_friends.user.presentation;

import com.example.hello_friends.common.response.Resp;
import com.example.hello_friends.user.application.Request.UserRequest;
import com.example.hello_friends.user.application.Service.UserService;
import com.example.hello_friends.user.application.Request.UserUpdateRequest;
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

    @Operation(summary = "회원가입", description = "회원가입합니다.")
    @PostMapping("/api/user/register")
    public User register(@RequestBody @Valid UserRequest userRequest){
        return userService.register(userRequest);
    }


    @Operation(summary = "사용자 정보 조회", description = "마이페이지에서 로그인한 회원의 정보를 확인합니다.")
    @GetMapping("/api/user/{id}")
    public User getUserInfo(@PathVariable Long id){
        return userService.findUserInformation(id);
    }

    @Operation(summary = "유저들 정보 조회", description = "회원들의 정보를 확인합니다.")
    @GetMapping("/api/admin/users")
    public Resp<List<User>> getUsersInfo(){
        return Resp.ok(userService.findUsersInformation());
    }

    @Operation(summary = "유저 정보 수정", description = "회원의 정보를 변경합니다.")
    @PutMapping("/api/user/{id}")
    public User updateUserInfo(@PathVariable Long id, @RequestBody UserUpdateRequest userUpdateRequest){
        return userService.updateUserInformation(id, userUpdateRequest);
    }

    @Operation(summary = "유저 탈퇴", description = "회원이 탈퇴합니다.")
    @DeleteMapping("/api/user/{id}")
    public Resp<String> deleteUser(@PathVariable Long id){
        userService.deleteUser(id);
        return Resp.ok("회원 정보가 삭제되었습니다.");
    }
}
