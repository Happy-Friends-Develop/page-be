package com.example.hello_friends.schedule.domain;

import com.example.hello_friends.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    // 예약 리스트 전부 다
    List<Reservation> findAllByUserId(Long userId);
    // 상태에 맞는 예약 리스트
    List<Reservation> findAllByUserIdAndStatus(Long userId, ReservationStatus status);
    // 상태 확인 후 예약 가능 여부
    boolean existsByUserAndScheduleAndStatus(User user, Schedule schedule, ReservationStatus status);
    // 모든 예약 조회
    @Query("SELECT r FROM Reservation r " +
            "JOIN FETCH r.schedule s " +
            "JOIN FETCH s.board b " +
            "JOIN FETCH r.user u " +
            "WHERE r.status = :status " +
            "ORDER BY r.createdAt DESC")
    List<Reservation> findAllByStatusWithDetails(@Param("status") ReservationStatus status);
}
