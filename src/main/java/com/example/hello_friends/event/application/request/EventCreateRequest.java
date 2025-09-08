package com.example.hello_friends.event.application.request;

import com.example.hello_friends.event.application.EventType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class EventCreateRequest {
    private String title;
    private String content;
    private EventType eventType;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
