package com.example.hello_friends.schedule.domain;

import com.example.hello_friends.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findAllByUserId(Long userId);
    boolean existsByUserAndSchedule(User user, Schedule schedule);

}
