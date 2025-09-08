package com.example.hello_friends.notification.application.service;

import com.example.hello_friends.notification.application.response.NotificationResponse;
import com.example.hello_friends.notification.domain.Notification;
import com.example.hello_friends.notification.domain.NotificationRepository;
import com.example.hello_friends.user.domain.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final ObjectMapper objectMapper; // JSON 변환을 위해 추가
    private static final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    // 구독 로직은 변경 없음
    public SseEmitter subscribe(Long userId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.put(userId, emitter);

        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));

        try {
            emitter.send(SseEmitter.event().name("connect").data("Connection successful"));
        } catch (IOException e) {
            emitters.remove(userId);
        }
        return emitter;
    }

    // *** 전송 로직 수정 ***
    public void send(User receiver, String content, String url) {
        Notification notification = notificationRepository.save(
                createNotification(receiver, content, url)
        );

        if (emitters.containsKey(receiver.getId())) {
            SseEmitter emitter = emitters.get(receiver.getId());
            try {
                // DTO를 JSON 문자열로 변환
                String jsonResponse = objectMapper.writeValueAsString(NotificationResponse.from(notification));

                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(jsonResponse)); // JSON 데이터를 전송
            } catch (IOException e) {
                emitters.remove(receiver.getId());
            }
        }
    }

    private Notification createNotification(User receiver, String content, String url) {
        return Notification.builder()
                .receiver(receiver)
                .content(content)
                .url(url)
                .isRead(false)
                .build();
    }
}
