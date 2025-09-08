package com.example.hello_friends.event.domain;

import com.example.hello_friends.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventParticipantRepository extends JpaRepository<EventParticipant, Long> {
    // 사용자와 이벤트로 참가 기록을 찾는 기능
    Optional<EventParticipant> findByUserAndEvent(User user, Event event);
    // 참가 기록이 존재하는지 확인하는 기능
    boolean existsByUserAndEvent(User user, Event event);
}
