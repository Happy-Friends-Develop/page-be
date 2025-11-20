package com.example.hello_friends.notification.presentation;

import com.example.hello_friends.common.response.Resp;
import com.example.hello_friends.notification.application.response.NotificationResponse;
import com.example.hello_friends.notification.application.service.NotificationService;
import com.example.hello_friends.security.annotation.Auth;
import com.example.hello_friends.security.filter.JwtPrincipalDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

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

    @Operation(summary = "나의 알림 조회", description = "나에게 온 알림 목록들을 조회합니다.")
    @GetMapping(value = "/api/user/notifications")
    public ResponseEntity<Resp<List<NotificationResponse>>> getNotification(
            @Parameter(hidden = true) @Auth JwtPrincipalDto jwtPrincipalDto
    ){
        return ResponseEntity.ok(Resp.ok(notificationService.readMyNotification(jwtPrincipalDto.getId())));
    }

    @Operation(summary = "알림 읽기 및 이동", description = "알림을 읽음 상태로 변경하고, 이동할 URL을 반환합니다.")
    @PostMapping("/api/user/notifications/{id}/read")
    public ResponseEntity<Resp<String>> readNotification(
            @Parameter(hidden = true) @Auth JwtPrincipalDto jwtPrincipalDto,
            @PathVariable Long id
    ) {
        String redirectUrl = notificationService.readNotification(jwtPrincipalDto.getId(), id);

        return ResponseEntity.ok(Resp.ok(redirectUrl));
    }
}
