package com.example.hello_friends.security.filter;

import com.example.hello_friends.security.jwt.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Profile;
import com.example.hello_friends.security.userdetail.Role;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Profile("!prod")
@Component
public class JwtFilterDev extends JwtFilter{
    private final JwtUtil jwtUtil;
    private final HandlerExceptionResolver resolver;

    public JwtFilterDev(HandlerExceptionResolver handlerExceptionResolver, JwtUtil jwtUtil) {
        this.resolver = handlerExceptionResolver;
        this.jwtUtil=jwtUtil;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        JwtPrincipalDto jwtPrincipalDto = new JwtPrincipalDto(1L,"ADMIN");
        Authentication usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(jwtPrincipalDto, null, List.of(new SimpleGrantedAuthority(Role.ADMIN.getRoleName()),new SimpleGrantedAuthority(Role.CUSTOMER.getRoleName()),new SimpleGrantedAuthority(Role.SELLER.getRoleName()),new SimpleGrantedAuthority( Role.ADMIN.getRoleName())));
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        chain.doFilter(request, response);

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
