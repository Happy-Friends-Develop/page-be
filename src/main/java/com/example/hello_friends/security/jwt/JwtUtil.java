package com.example.hello_friends.security.jwt;


import com.example.hello_friends.security.filter.JwtPrincipalDto;
import com.example.hello_friends.security.userdetail.AbstractPrincipal;
import com.example.hello_friends.security.userdetail.UserPrincipal;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import com.example.hello_friends.security.userdetail.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtUtil {
    private final JwtConfig jwtConfig;

    public SecretKey getSecretKey() {
        SecretKey secretKey = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
        return secretKey;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String generateToken(JwtPrincipalDto jwtPrincipalDto) {
        return Jwts.builder()
                .subject("happy-friends token")
                .claim("id", jwtPrincipalDto.getId())
                .claim("type", jwtPrincipalDto.getType())
                .claim("authorities", Role.ADMIN.getRoleName())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * jwtConfig.getSessionMinute()))
                .signWith(getSecretKey())
                .compact();
    }

    public String generateToken(AbstractPrincipal principal) {
        if (principal instanceof UserPrincipal) {
            UserPrincipal userPrincipal = (UserPrincipal) principal;
            return Jwts.builder()
                    .subject("happy-friends token")
                    .claim("id", userPrincipal.getId())
                    .claim("type", userPrincipal.getType())
                    .claim("authorities", userPrincipal.getAuthorities().stream().map(
                            GrantedAuthority::getAuthority
                    ).collect(Collectors.joining(",")))
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * jwtConfig.getSessionMinute()))
                    .signWith(getSecretKey())
                    .compact();
        }
        throw new IllegalArgumentException("Unsupported principal type: " + principal.getClass());
    }

    public Claims extractClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) { // 모든 JWT 관련 예외를 한 번에 처리
            if (e instanceof SecurityException || e instanceof MalformedJwtException) {
                log.error("Invalid JWT Token: {}", e.getMessage());
            } else if (e instanceof ExpiredJwtException) {
                log.error("Expired JWT Token: {}", e.getMessage());
            } else if (e instanceof UnsupportedJwtException) {
                log.error("Unsupported JWT Token: {}", e.getMessage());
            } else if (e instanceof IllegalArgumentException) {
                log.error("JWT claims string is empty: {}", e.getMessage());
            } else {
                log.error("Unknown JWT Exception: {}", e.getMessage());
            }
            throw e;
        }
    }


    public Long getUserId(Claims claims) {
        return claims.get("id", Long.class);
    }

    public String getLoginType(Claims claims) {
        return claims.get("type", String.class);
    }

    public String getAuthorities(Claims claims) {
        return claims.get("authorities", String.class
        );
    }
}
