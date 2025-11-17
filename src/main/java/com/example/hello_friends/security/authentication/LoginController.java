package com.example.hello_friends.security.authentication;

import com.example.hello_friends.common.response.Resp;
import com.example.hello_friends.security.jwt.JwtUtil;
import com.example.hello_friends.security.userdetail.AbstractPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "인증")
public class LoginController {
    private final AuthenticationProvideImpl authenticationProvide;
    private final JwtUtil jwtUtil;

    @PostMapping("/api/auth")
    @Operation(summary = "로그인 기능", description = "사용자에 대해 공통된 로그인 엔드포인트를 제공하며, JWT 토큰을 반환한다. JWT 토큰은 'Authorization' 헤더에 삽입해 사용합니다.")
    Resp<String> login(@Valid @RequestBody LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken unauthenticated = UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.getLoginId(), loginRequest.getPwd());
        Authentication authenticate = authenticationProvide.authenticate(unauthenticated);
        if (null == authenticate || !authenticate.isAuthenticated())
            throw new BadCredentialsException("로그인에 실패 했습니다.");

        AbstractPrincipal customUserDetail = (AbstractPrincipal) authenticate.getPrincipal();
        return Resp.ok(jwtUtil.generateToken(customUserDetail));
    }
}
