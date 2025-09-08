package com.example.hello_friends.notification.presentation;

import com.example.hello_friends.notification.application.service.NotificationService;
import com.example.hello_friends.security.annotation.Auth;
import com.example.hello_friends.security.filter.JwtPrincipalDto;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @Operation(summary = "알림 조회", description = "알림을 조회합니다.")
    @GetMapping(value = "/api/user/notification/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@Parameter(hidden = true) @Auth JwtPrincipalDto jwtPrincipalDto) {
        Long userId = jwtPrincipalDto.getId();
        return notificationService.subscribe(userId);
    }
}
