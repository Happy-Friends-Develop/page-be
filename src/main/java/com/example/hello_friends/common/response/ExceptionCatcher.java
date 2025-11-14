package com.example.hello_friends.common.response;

import com.example.hello_friends.security.handler.AuthorizationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.AuthenticationException;

@Slf4j
@RestControllerAdvice
// 예외 처리용 Class
public class ExceptionCatcher {
    @ExceptionHandler(value = {AuthenticationException.class})
    public ResponseEntity<?> handleAuthenticationException(AuthenticationException e) {
        log.error("handleAuthenticationException() in GlobalExceptionHandler throw CustomException : {}", e.getMessage());
        return  new ResponseEntity<>(Resp.fail(e), HttpStatus.UNAUTHORIZED );
    }


    @ExceptionHandler(value = {AccessDeniedException.class})
    public ResponseEntity<?> handleAuthorizationException(AccessDeniedException e) {
        log.error("handleAuthorizationException() in GlobalExceptionHandler throw CustomException : {}", e.getMessage());
        return  new ResponseEntity<>(Resp.fail(new AuthorizationException()),HttpStatus.UNAUTHORIZED );
    }


    // 커스텀 예외
    @ExceptionHandler(value = {MotherException.class})
    public ResponseEntity<?> handleCustomException(MotherException e) {
        log.error("handleCustomException() in GlobalExceptionHandler throw CustomException : {}", e.getMessage());
        // 예외 정보랑 HTTP Status 같이 반환
        return  new ResponseEntity<>(Resp.fail(e),e.getHttpStatus() );
    }


    // 기본 예외(커스텀 제외)
    @ExceptionHandler(value = {RuntimeException.class,Exception.class})
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<?> handleException(Exception e) {
        log.error("handleException() in GlobalExceptionHandler throw Exception : {}", e.getMessage());
        // 일반 예외를 MotherException으로 변환
        MotherException motherException;
        if(e instanceof  RuntimeException)
            motherException = new MotherException(e.getMessage(), HttpStatus.BAD_REQUEST);
        else
            motherException = new MotherException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        e.printStackTrace();
        return new ResponseEntity<>(Resp.fail(motherException), motherException.getHttpStatus());
    }
}


