package com.example.hello_friends.security.authentication;

import com.example.hello_friends.security.userdetail.UserPrincipal;
import com.example.hello_friends.user.domain.User;
import com.example.hello_friends.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AuthenticationProvideImpl implements AuthenticationProvider {
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String loginId = authentication.getName();
        String pwd = authentication.getCredentials().toString();

        UserDetails userDetails = userDetailsService.loadUserByUsername(loginId);

        if (!userDetails.isEnabled()) {
            throw new DisabledException("계정이 활성화되어 있지 않습니다. (휴면 또는 삭제됨)");
        }

        if (passwordEncoder.matches(pwd, userDetails.getPassword())) {

            UserPrincipal userPrincipal = (UserPrincipal) userDetails;

            Long authId = userPrincipal.getId();

            User user = userRepository.findByAuthId(authId)
                    .orElseThrow(() -> new BadCredentialsException("사용자 정보를 찾을 수 없습니다. (데이터 불일치)"));

            // 사용자 마지막 로그인 시간 업데이트
            user.updateLastLoginDate();

            return new UsernamePasswordAuthenticationToken(userDetails, pwd, userDetails.getAuthorities());
        }

        throw new BadCredentialsException("비밀 번호가 일치하지 않습니다.");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
