package com.example.hello_friends.event.application.response;

import com.example.hello_friends.event.application.EventType;
import com.example.hello_friends.event.domain.Event;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventResponse {
    private final Long id;
    private final String title;
    private final String content;
    private final EventType eventType;
    private final String authorNickname;
    private final int participantCount;
    private final LocalDateTime createdAt;

    @Builder
    private EventResponse(Long id, String title, String content, EventType eventType, String authorNickname, int participantCount, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.eventType = eventType;
        this.authorNickname = authorNickname;
        this.participantCount = participantCount;
        this.createdAt = createdAt;
    }

    public static EventResponse from(Event event) {
        return EventResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .content(event.getContent())
                .eventType(event.getEventType())
                .authorNickname(event.getAuthor().getNickname())
                .participantCount(event.getParticipants().size())
                .createdAt(event.getCreatedAt())
                .build();
    }
}
