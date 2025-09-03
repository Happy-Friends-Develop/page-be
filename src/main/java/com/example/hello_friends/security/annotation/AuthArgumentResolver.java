package com.example.hello_friends.security.annotation;

import org.springframework.stereotype.Component;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @Auth 어노테이션에 대한 Argument Resolver.
 * 컨트롤러 메소드의 파라미터에 @Auth 어노테이션이 사용되면,
 * 현재 인증된 사용자 정보를 해당 파라미터에 주입하는 역할 수행.
 */
@Component
public class AuthArgumentResolver implements HandlerMethodArgumentResolver {

    /**
     * 특정 파라미터를 이 Argument Resolver가 지원하는지 여부 결정
     * @param parameter 검사할 컨트롤러 메소드의 파라미터
     * @return 파라미터에 @Auth 어노테이션이 있으면 true 반환, 그렇지 않으면 false 반환
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // parameter.hasParameterAnnotation(Auth.class)를 호출하여
        // @Auth 어노테이션의 존재 여부 확인.
        return parameter.hasParameterAnnotation(Auth.class);
    }

    /**
     * supportsParameter 메소드가 true를 반환했을 때, 파라미터에 주입할 실제 객체 생성 후 반환
     * @param parameter          메소드 파라미터 정보
     * @param mavContainer       ModelAndView 컨테이너
     * @param webRequest         현재 요청(request) 정보
     * @param binderFactory      WebDataBinder를 생성하는 팩토리
     * @return 인증된 사용자의 Principal 객체를 반환하여 파라미터로 주입
     * @throws Exception UnauthenticatedException을 포함한 예외를 던질 수 있음
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        // SecurityContextHolder에서 현재 요청 스레드의 Authentication(인증 정보) 객체 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Authentication 객체가 null이거나, 인증되지 않은 상태이거나, 익명 사용자인 경우,
        // UnauthenticatedException을 발생시켜 인증되지 않은 접근임을 알림
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new UnauthenticatedException();
        }

        // 모든 인증 검사를 통과하면, Authentication 객체에서 Principal(사용자 주체)을 꺼내 반환
        // 여기서 반환된 값이 @Auth 어노테이션이 붙은 파라미터로 전달됨
        return authentication.getPrincipal();
    }
}