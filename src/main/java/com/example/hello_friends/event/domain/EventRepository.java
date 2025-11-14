package com.example.hello_friends.event.domain;

import com.example.hello_friends.event.application.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByEventType(EventType eventType);
}
