package com.example.hello_friends.security.filter;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JwtPrincipalDto {
    @NotBlank
    @Schema(description = "아이디", example = "5")
    private Long id; // id

    @NotBlank
    @Schema(description = "타입", example = "USER")
    private String type; // USER

    public JwtPrincipalDto(Long id, String type) {
        this.type = type;
        this.id = id;
    }
}
