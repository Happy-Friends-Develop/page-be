package com.example.hello_friends.security.handler;

import com.example.hello_friends.common.response.MotherException;
import org.springframework.http.HttpStatus;

public class AuthorizationException extends MotherException {
    public static final String MESSAGE = "사용권한이 없습니다.";
    public AuthorizationException() {
        super(MESSAGE, HttpStatus.FORBIDDEN);
    }
}
