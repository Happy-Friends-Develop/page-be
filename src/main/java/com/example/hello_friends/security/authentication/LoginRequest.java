package com.example.hello_friends.security.authentication;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LoginRequest {
    @NotBlank
    @Schema(description = "로그인 아이디", example = "dev")
    private String loginId;

    @NotBlank
    @Schema(description = "로그인 비밀번호", example = "@Malsdud0414")
    private String pwd;
}
