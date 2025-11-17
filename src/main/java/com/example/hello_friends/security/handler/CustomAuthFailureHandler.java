package com.example.hello_friends.security.handler;

import com.example.hello_friends.common.exception.AccountDormantException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        String failureUrl = "/login-form?error=true"; // 기본 실패 URL (아이디/비번 틀림) 추후 수정예정

        // 휴면 계정 예외인 경우
        if (exception instanceof AccountDormantException) {

            // 예외에서 loginId를 추출
            String loginId = ((AccountDormantException) exception).getLoginId();

            // 이 loginId를 세션에 저장
            request.getSession().setAttribute("dormantLoginId", loginId);

            // '휴면 계정 복구 전용 페이지'로 리다이렉트
            failureUrl = "/members/reactivate";

        } else if (exception instanceof DisabledException) {
            // (탈퇴한 계정)
            failureUrl = "/login-form?error=withdrawn";
        }

        response.sendRedirect(failureUrl);
    }
}
