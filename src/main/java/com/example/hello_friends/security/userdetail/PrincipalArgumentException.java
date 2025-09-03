package com.example.hello_friends.security.userdetail;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class PrincipalArgumentException extends UsernameNotFoundException {
    private static final String MESSAGE = "토큰 필수 값이 누락 되었습니다.";
    public PrincipalArgumentException() {
        super(MESSAGE);
    }
}
