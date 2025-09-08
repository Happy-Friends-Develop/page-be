package com.example.hello_friends.event.domain;

import com.example.hello_friends.common.entity.LogEntity;
import com.example.hello_friends.event.application.EventType;
import com.example.hello_friends.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Event extends LogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Lob // 긴 내용을 저장할 수 있도록 설정
    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING) // Enum 타입을 문자열로 저장 (가독성/안정성)
    @Column(nullable = false)
    private EventType eventType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User author; // 작성자

    // cascade, orphanRemoval: 이벤트가 삭제되면 참가자 명단도 함께 삭제
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventParticipant> participants = new ArrayList<>();


    public Event(String title, String content, EventType eventType, User author) {
        this.title = title;
        this.content = content;
        this.eventType = eventType;
        this.author = author;
    }

    public void update(String title, String content){
        this.title = title;
        this.content = content;
    }
}
