package com.example.hello_friends.schedule.domain;

import com.example.hello_friends.board.domain.Board;
import com.example.hello_friends.common.entity.LogEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Schedule extends LogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board; // 이 스케줄이 속한 게시글

    @Column(nullable = false)
    private LocalDate scheduleDate; // 옵션 날짜 (예: 2025-01-01)

    @Column(nullable = false)
    private int maxHeadcount; // 총 정원

    @Column(nullable = false)
    private int currentHeadcount; // 현재 예약된 인원

    @Column(nullable = false)
    private BigDecimal price; // 1명 당 가격

    // 정원이 다 찼는지 확인하는 메소드
    public boolean isFull(int quantity) {
        return this.currentHeadcount + quantity > this.maxHeadcount;
    }

    // 예약 시 현재 인원을 증가시키는 메소드
    public void addReservation(int quantity) {
        this.currentHeadcount += quantity;
    }

    // 예약 취소 시 현재 인원을 감소시키는 메소드
    public void cancelReservation(int quantity) {
        this.currentHeadcount -= quantity;
    }

    public Schedule(Board board, LocalDate scheduleDate, int maxHeadcount, BigDecimal price){
        this.board = board;
        this.scheduleDate = scheduleDate;
        this.maxHeadcount = maxHeadcount;
        this.currentHeadcount = 0;
        this.price = price;
    }

    public void update(LocalDate scheduleDate, int maxHeadcount, BigDecimal price) {
        this.scheduleDate = scheduleDate;
        this.maxHeadcount = maxHeadcount;
        this.price = price;
    }
}
