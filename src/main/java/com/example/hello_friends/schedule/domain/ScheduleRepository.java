package com.example.hello_friends.schedule.domain;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findAllByBoardId(Long boardId);

    // 비관적 락 추가
    // 동시에 누르는 경우 방지
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    // 3초 동안만 기다림
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "3000")})
    @Query("select s from Schedule s where s.id = :id")
    Optional<Schedule> findByIdWithLock(@Param("id") Long id);
}
