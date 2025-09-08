package com.example.hello_friends.security.filter;

import com.example.hello_friends.security.jwt.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper; // ObjectMapper import
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@Profile("prod")
public class JwtFilterProd extends JwtFilter {
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    public JwtFilterProd(JwtUtil jwtUtil, ObjectMapper objectMapper) {
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        try {
            String authHeader = request.getHeader("Authorization");

            if (authHeader == null) {
                throw new JwtTokenNotFoundException();
            }

            Claims payload = jwtUtil.extractClaims(authHeader);
            String loginType = jwtUtil.getLoginType(payload);
            Long id = jwtUtil.getUserId(payload);

            JwtPrincipalDto jwtPrincipalDto = new JwtPrincipalDto(id, loginType);

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    jwtPrincipalDto,
                    null,
                    AuthorityUtils.commaSeparatedStringToAuthorityList(jwtUtil.getAuthorities(payload))
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);

        } catch (Exception e) {
            // resolver를 사용하는 대신, 직접 에러 응답을 생성
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 상태 코드 설정
            response.setContentType(MediaType.APPLICATION_JSON_VALUE); // 응답 형식을 JSON으로 강제
            response.setCharacterEncoding("UTF-8"); // 인코딩 설정

            // 클라이언트에게 보낼 에러 메시지 생성
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("status", HttpServletResponse.SC_UNAUTHORIZED);
            errorDetails.put("error", "Unauthorized");
            errorDetails.put("message", "인증이 필요합니다: " + e.getMessage());
            errorDetails.put("path", request.getRequestURI());

            // ObjectMapper를 사용해 JSON 형태로 응답 본문에 쓰기
            objectMapper.writeValue(response.getWriter(), errorDetails);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        String method = request.getMethod();

        if (path.startsWith("/api/auth") && method.equals("POST")) {
            return true;
        }
        else if (path.startsWith("/api/resources") && method.equals("GET")) {
            return true;
        }
        else if (path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs")) {
            return true;
        }
        return false;
    }
}