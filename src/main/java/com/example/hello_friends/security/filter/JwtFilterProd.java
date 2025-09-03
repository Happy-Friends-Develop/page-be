package com.example.hello_friends.security.filter;

import com.example.hello_friends.security.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Optional;

@Component
@Profile("prod")
public class JwtFilterProd extends JwtFilter {
    private final JwtUtil jwtUtil;
    private final HandlerExceptionResolver resolver;

    public JwtFilterProd(HandlerExceptionResolver handlerExceptionResolver, JwtUtil jwtUtil) {
        this.resolver = handlerExceptionResolver;
        this.jwtUtil=jwtUtil;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        try {
            String authHeader = request.getHeader("Authorization");

            if (authHeader == null ) {
                throw new JwtTokenNotFoundException();
            }

            Claims payload = jwtUtil.extractClaims(authHeader);
            String loginType = jwtUtil.getLoginType(payload);
            Long id = jwtUtil.getUserId(payload);

            JwtPrincipalDto jwtPrincipalDto = new JwtPrincipalDto(id, loginType);

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    jwtPrincipalDto
                    , null,
                    AuthorityUtils.commaSeparatedStringToAuthorityList(jwtUtil.getAuthorities(payload)));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);

        }catch (Exception e){
            resolver.resolveException(request,response,null,e);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        String method = request.getMethod();

        if (path.startsWith("/api/auth") && method.equals("POST")){
            return true;
        }
        else if(path.startsWith("/api/resources")&& method.equals("GET")){
            return true;
        }
        else if (path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs")) {
            return true;
        }


        return false;
    }
}
