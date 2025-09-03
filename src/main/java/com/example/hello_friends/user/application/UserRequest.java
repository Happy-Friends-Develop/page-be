package com.example.hello_friends.user.application;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRequest {
    @NotBlank
    @Schema(description = "아이디", example = "dev")
    private String id;

    @NotBlank
    @Schema(description = "비밀번호", example = "1111")
    private String password;

    @NotBlank
    @Schema(description = "이름", example = "개발용 계정")
    private String name;

    @NotBlank
    @Schema(description = "닉네임", example = "dev")
    private String nickname;

    @NotBlank
    @Schema(description = "전화번호", example = "010-5380-0048")
    private String phone;

    @NotBlank
    @Schema(description = "이메일", example = "0414minyoung@naver.com")
    private String email;

    @NotBlank
    @Schema(description = "주소", example = "인천 남동구 백범로 124번길 43")
    private String address;
}
