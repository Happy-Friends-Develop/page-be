package com.example.hello_friends.auth.application;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthBody {
    String loginId;
    String pwd;

    AuthBody() {}

    // 로그인 ID와 비밀번호로 AuthBody 객체 생성
    public static AuthBody of(String loginId,String pwd){
        return new AuthBody(loginId,pwd);
    }
}

