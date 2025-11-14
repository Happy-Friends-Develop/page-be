package com.example.hello_friends.common.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;

public record Resp<T>(
        @JsonIgnore  // JSON 변환 시 이 필드는 제외됨
        HttpStatus httpStatus,  // HTTP 상태 코드
        boolean success,  // 요청 처리 성공 여부

        @Nullable T data,  // 응답 데이터 (null 허용)
        @Nullable String errorMessage,  // 오류 메시지 (null 허용)
        LocalDateTime serverDateTime  // 서버 응답 시간
) {
    // 성공시
    public static <T> Resp<T> ok(@Nullable T data){
        return new Resp<T>(HttpStatus.OK,true,data,null,LocalDateTime.now());
    }

    // 실패시
    public static <T extends Exception> Resp<T> fail(@Nullable T e){
        return new Resp<T>(HttpStatus.BAD_REQUEST,false,null,e.getMessage(),LocalDateTime.now());
    }

    public static <T> Resp<T> fail(@Nullable String errorMessage) {
        return new Resp<>(HttpStatus.BAD_REQUEST, false, null, errorMessage, LocalDateTime.now());
    }

}