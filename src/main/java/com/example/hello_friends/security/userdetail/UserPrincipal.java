package com.example.hello_friends.security.userdetail;

import com.example.hello_friends.user.domain.MemberStatus; // ✨ (O) MemberStatus 임포트
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class UserPrincipal extends AbstractPrincipal {
    private final String type = "USER";
    private final MemberStatus userStatus;

    public static UserPrincipal of(Long id, String loginId, String password,
                                   Collection<? extends GrantedAuthority> authorities,
                                   MemberStatus userStatus) {
        if (id == null || loginId == null || password == null) {
            throw new PrincipalArgumentException();
        }
        return new UserPrincipal(id, loginId, password, authorities, userStatus);
    }

    private UserPrincipal(Long id, String loginId, String password,
                          Collection<? extends GrantedAuthority> authorities,
                          MemberStatus userStatus) {
        super(id, loginId, password, authorities);
        this.userStatus = userStatus;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public boolean isEnabled() {
        // 회원의 상태가 ACTIVE일 때만 true 반환
        return this.userStatus == MemberStatus.ACTIVE;
    }
}