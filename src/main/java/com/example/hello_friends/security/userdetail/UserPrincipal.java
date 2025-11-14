package com.example.hello_friends.security.userdetail;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class UserPrincipal extends AbstractPrincipal {
    private final String type = "USER";

    public static UserPrincipal of(Long id, String loginId, String password, Collection<? extends GrantedAuthority> authorities) {
        if (id == null ||  loginId == null || password == null) {
            throw new PrincipalArgumentException();
        }
        return new UserPrincipal(id, loginId, password, authorities);
    }

    private UserPrincipal(Long id, String loginId, String password, Collection<? extends GrantedAuthority> authorities) {
        super(id, loginId, password, authorities);

    }

    @Override
    public String getType() {
        return this.type;

    }
}
