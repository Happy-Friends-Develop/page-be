package com.example.hello_friends.security.jwt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="security.jwt")
@Getter
@RequiredArgsConstructor
public class JwtConfig {
    private final String secret;
    private final Integer sessionMinute; // minute
}
