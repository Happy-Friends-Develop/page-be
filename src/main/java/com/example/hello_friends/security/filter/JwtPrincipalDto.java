package com.example.hello_friends.security.filter;

import lombok.Data;

@Data
public class JwtPrincipalDto {
    private Long id; // id
    private String type; // KIOSK, USER


    public JwtPrincipalDto(Long id, String type) {
        this.type = type;
        this.id = id;
    }
}
