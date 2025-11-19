package com.example.hello_friends.schedule.domain;

import com.example.hello_friends.common.entity.LogEntity;
import com.example.hello_friends.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Reservation extends LogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 예약한 사용자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule; // 예약한 스케줄 (예: 1월 1일 옵션)

    @Column(nullable = false)
    private int quantity; // 예약한 인원 수

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    public Reservation(User user, Schedule schedule, int quantity){
        this.user = user;
        this.schedule = schedule;
        this.quantity = quantity;
        this.status = ReservationStatus.RESERVED;
    }

    public void cancel() {
        this.status = ReservationStatus.CANCELED;
    }
}
