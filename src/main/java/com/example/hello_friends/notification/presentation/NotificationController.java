package com.example.hello_friends.notification.presentation;

import com.example.hello_friends.notification.application.service.NotificationService;
import com.example.hello_friends.security.annotation.Auth;
import com.example.hello_friends.security.filter.JwtPrincipalDto;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@Tag(name = "알림 조회")
public class NotificationController {
    private final NotificationService notificationService;

    @Operation(summary = "실시간 알림을 보내는 기능", description = "실시간 알림을 보내는 기능입니다.")
    @GetMapping(value = "/api/user/notification/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@Parameter(hidden = true) @Auth JwtPrincipalDto jwtPrincipalDto) {
        Long userId = jwtPrincipalDto.getId();
        return notificationService.subscribe(userId);
    }
}
