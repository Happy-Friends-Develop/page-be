package com.example.hello_friends.security.filter;

import com.example.hello_friends.common.response.MotherException;
import org.springframework.http.HttpStatus;

public class JwtTokenNotFoundException extends MotherException {
    private static final String MESSAGE ="토큰이 헤더에 존재하지 않습니다.";
    public JwtTokenNotFoundException( ) {
        super(MESSAGE, HttpStatus.UNAUTHORIZED);
    }
}
