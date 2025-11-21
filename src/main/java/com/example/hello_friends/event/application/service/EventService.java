package com.example.hello_friends.event.application.service;

import com.example.hello_friends.common.exception.EventNotFoundException;
import com.example.hello_friends.common.exception.NoAuthorityException;
import com.example.hello_friends.common.exception.UserNotFoundException;
import com.example.hello_friends.common.response.MotherException;
import com.example.hello_friends.event.application.EventType;
import com.example.hello_friends.event.application.request.EventCreateRequest;
import com.example.hello_friends.event.application.response.EventResponse;
import com.example.hello_friends.event.domain.Event;
import com.example.hello_friends.event.domain.EventParticipant;
import com.example.hello_friends.event.domain.EventParticipantRepository;
import com.example.hello_friends.event.domain.EventRepository;
import com.example.hello_friends.user.domain.User;
import com.example.hello_friends.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventParticipantRepository eventParticipantRepository;
    private final UserRepository userRepository;

    // 새로운 이벤트 또는 공지사항을 생성
    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public EventResponse createEvent(EventCreateRequest request, Long authorId, EventType eventType) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new UserNotFoundException("작성자를 찾을 수 없습니다. ID : " + authorId));

        Event event = new Event(request.getTitle(), request.getContent(), eventType, author, request.getStartDate(), request.getEndDate());

        Event savedEvent = eventRepository.save(event);
        return EventResponse.from(savedEvent);
    }

    @Transactional
    public EventResponse updateEvent(EventCreateRequest request, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("해당 이벤트를 찾을 수 없습니다. ID : " + eventId));

        event.update(request.getTitle(), request.getTitle(), request.getStartDate(), request.getEndDate());

        return EventResponse.from(event);
    }

    // 특정 ID를 가진 이벤트의 상세 정보를 조회합니다.
    @Transactional(readOnly = true)
    public EventResponse getEventById(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("해당 이벤트를 찾을 수 없습니다. ID : " + eventId));

        return EventResponse.from(event);
    }

    // 모든 이벤트와 공지사항 목록을 조회합니다.
    @Transactional(readOnly = true)
    public List<EventResponse> getAllEvents(EventType eventType) {
        List<Event> events;
        if (eventType == null) {
            // type 파라미터가 없으면 전체 조회
            events = eventRepository.findAll();
        } else {
            // type 파라미터가 있으면 해당 타입만 조회
            events = eventRepository.findAllByEventType(eventType);
        }
        return events.stream()
                .map(EventResponse::from)
                .collect(Collectors.toList());
    }

    // 사용자가 특정 이벤트에 참가 신청을 합니다.
    @Transactional
    public void joinEvent(Long eventId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다. ID : " + userId));
        Event event = eventRepository.findByIdWithLock(eventId)
                .orElseThrow(() -> new EventNotFoundException("이벤트를 찾을 수 없습니다. ID : " + eventId));

        if (event.getEventType() == EventType.NOTICE) {
            throw new NoAuthorityException("공지사항에는 참여할 수 없습니다.");
        }

        LocalDateTime now = LocalDateTime.now();
        // 현재 시간이 이벤트 시작 전이거나 종료 후인 경우 예외 발생
        if (now.isBefore(event.getStartDate()) || now.isAfter(event.getEndDate())) {
            throw new MotherException("이벤트 참여 기간이 아닙니다.", HttpStatus.BAD_REQUEST);
        }

        if (eventParticipantRepository.existsByUserAndEvent(user, event)) {
            throw new MotherException("이미 참여한 이벤트입니다.", HttpStatus.BAD_REQUEST);
        }

        EventParticipant participant = new EventParticipant(user, event);
        eventParticipantRepository.save(participant);
    }

    // 사용자가 이벤트 참가를 취소합니다.
    @Transactional
    public void cancelEventParticipation(Long eventId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다. ID : " + userId));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("이벤트를 찾을 수 없습니다. ID : " + eventId));

        // 사용자의 참가 기록을 찾고 없으면 예외를 발생
        EventParticipant participant = eventParticipantRepository.findByUserAndEvent(user, event)
                .orElseThrow(() -> new MotherException("이벤트에 참여한 기록이 없습니다.", HttpStatus.BAD_REQUEST));

        // 찾은 참가 기록을 데이터베이스에서 삭제
        eventParticipantRepository.delete(participant);
    }
}
