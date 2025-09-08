package com.example.hello_friends.event.domain;

import com.example.hello_friends.common.entity.LogEntity;
import com.example.hello_friends.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// 한 사용자가 같은 이벤트에 중복으로 참여하는 것을 방지
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "event_id"}))
public class EventParticipant extends LogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 참가한 사용자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event; // 참여한 이벤트

    public EventParticipant(User user, Event event) {
        this.user = user;
        this.event = event;
    }
}