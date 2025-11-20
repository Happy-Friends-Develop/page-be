package com.example.hello_friends.notification.application.service;

import com.example.hello_friends.common.exception.NoAuthorityException;
import com.example.hello_friends.common.exception.NotificationNotFoundException;
import com.example.hello_friends.common.exception.UserNotFoundException;
import com.example.hello_friends.notification.application.response.NotificationResponse;
import com.example.hello_friends.notification.domain.Notification;
import com.example.hello_friends.notification.domain.NotificationRepository;
import com.example.hello_friends.user.domain.User;
import com.example.hello_friends.user.domain.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;

    private static final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    // 구독 로직
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

    // url 있는 알림 전송 로직
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

    // url 없는 알림 전송 로직
    public void send(User receiver, String content){
        Notification notification = notificationRepository.save(
                createNotification(receiver, content)
        );

        if (emitters.containsKey(receiver.getId())){
            SseEmitter emitter = emitters.get(receiver.getId());
            try {
                // DTO를 JSON 문자열로 변환
                String jsonResponse = objectMapper.writeValueAsString(NotificationResponse.noUrlFrom(notification));

                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(jsonResponse)); // JSON 데이터를 전송
            } catch (IOException e) {
                emitters.remove(receiver.getId());
            }
        }
    }

    // url 존재하는 알림
    private Notification createNotification(User receiver, String content, String url) {
        return Notification.builder()
                .receiver(receiver)
                .content(content)
                .url(url)
                .isRead(false)
                .build();
    }

    // url 없는 알림(Overloading)
    private Notification createNotification(User receiver, String content){
        return Notification.builder()
                .receiver(receiver)
                .content(content)
                .isRead(false)
                .build();
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> readMyNotification(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("해당 사용자를 찾을 수 없습니다. ID : " + userId));

        List<Notification> notifications = notificationRepository.findAllByReceiverIdOrderByCreatedAtDesc(userId);

        return notifications.stream()
                .map(NotificationResponse::from)
                .toList();
    }

    // 알림 단건 읽기 (클릭 시 호출)
    @Transactional
    public String readNotification(Long userId, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException("존재하지 않는 알림입니다."));

        if (!notification.getReceiver().getId().equals(userId)) {
            throw new NoAuthorityException("해당 알림을 읽을 권한이 없습니다.");
        }

        // 읽음 처리
        notification.read();

        // url로 이동
        return notification.getUrl();
    }
}
