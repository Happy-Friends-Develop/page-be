package com.example.hello_friends.security.userdetail;

import org.springframework.security.core.userdetails.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
public abstract class AbstractPrincipal extends User {
    private Long id;

    public AbstractPrincipal(Long id, String loginId, String password, Collection<? extends GrantedAuthority> authorities) {
        super(loginId, password, authorities);
        this.id = id;
    }

    public abstract String getType();
}
