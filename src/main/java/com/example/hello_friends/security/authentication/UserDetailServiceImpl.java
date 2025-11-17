package com.example.hello_friends.security.authentication;

import com.example.hello_friends.auth.domain.Auth;
import com.example.hello_friends.auth.domain.AuthRepository;
import com.example.hello_friends.security.userdetail.PrincipalArgumentException;
import com.example.hello_friends.security.userdetail.UserPrincipal;
import com.example.hello_friends.user.domain.User;
import com.example.hello_friends.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {
    private final AuthRepository authRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        Auth auth = authRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("계정 정보가 없습니다. (loginId: " + loginId + ")"));

        User user = userRepository.findByAuthId(auth.getId())
                .orElseThrow(() -> new UsernameNotFoundException("연결된 사용자 정보가 없습니다. (authId: " + auth.getId() + ")"));

        try {
            return UserPrincipal.of(
                    auth.getId(),
                    auth.getLoginId(),
                    auth.getPwd(),
                    List.of(new SimpleGrantedAuthority(user.getUserRole().toRole().getRoleName())),
                    user.getStatus()
            );
        } catch (PrincipalArgumentException e) {
            throw new PrincipalArgumentException();
        } catch (UsernameNotFoundException e) {
            throw new UsernameNotFoundException("계정 정보가 없습니다.");
        }
    }
}