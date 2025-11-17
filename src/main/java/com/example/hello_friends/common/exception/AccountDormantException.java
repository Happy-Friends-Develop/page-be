package com.example.hello_friends.common.exception;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class AccountDormantException extends AuthenticationException {

    // 복구 절차에 사용할 수 있도록 loginId를 저장
    private final String loginId;

    public AccountDormantException(String msg, String loginId) {
        super(msg);
        this.loginId = loginId;
    }

}
