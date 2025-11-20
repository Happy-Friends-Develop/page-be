package com.example.hello_friends.notification.domain;

import com.example.hello_friends.common.entity.LogEntity;
import com.example.hello_friends.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends LogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(nullable = true)
    private String url;

    @Column(nullable = false)
    private boolean isRead;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User receiver;


    @Builder
    public Notification(User receiver, String content, String url, boolean isRead) {
        this.receiver = receiver;
        this.content = content;
        this.url = url;
        this.isRead = isRead;
    }

    public void read() {
        this.isRead = true;
    }
}