package com.example.hello_friends.security.annotation;

import com.example.hello_friends.common.response.MotherException;
import org.springframework.http.HttpStatus;

public class UnauthenticatedException extends MotherException {

    public static String MESSAGE ="로그인이 필요한 요청입니다.";
    public UnauthenticatedException() {
        super(MESSAGE, HttpStatus.UNAUTHORIZED);
    }
}
