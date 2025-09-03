package com.example.hello_friends.user.presentation;

import com.example.hello_friends.user.application.UserRequest;
import com.example.hello_friends.user.application.UserService;
import com.example.hello_friends.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "사용자")
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    @Operation(summary = "회원가입", description = "회원가입합니다.")
    @PostMapping("register")
    public User register(@RequestBody @Valid UserRequest userRequest){
        return userService.register(userRequest);
    }

}
