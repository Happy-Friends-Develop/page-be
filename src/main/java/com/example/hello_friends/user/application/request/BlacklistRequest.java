package com.example.hello_friends.user.application.request;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public record BlacklistRequest(
        String reason,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime endDate
) {}
