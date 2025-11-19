package com.example.hello_friends.notification.application.response;

import com.example.hello_friends.notification.domain.Notification;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationResponse {
    private Long id;
    private String content;
    private String url;
    private boolean isRead;
    private LocalDateTime createdAt;

    @Builder
    private NotificationResponse(Long id, String content, String url, boolean isRead, LocalDateTime createdAt) {
        this.id = id;
        this.content = content;
        this.url = url;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    public static NotificationResponse from(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .content(notification.getContent())
                .url(notification.getUrl())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }

    public static NotificationResponse noUrlFrom(Notification notification){
        return NotificationResponse.builder()
                .id(notification.getId())
                .content(notification.getContent())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
