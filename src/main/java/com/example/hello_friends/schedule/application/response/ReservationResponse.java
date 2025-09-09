package com.example.hello_friends.schedule.application.response;

import com.example.hello_friends.schedule.domain.Reservation;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ReservationResponse {
    private final Long reservationId;
    private final String boardTitle;
    private final LocalDate scheduleDate;
    private final int quantity;
    private final BigDecimal totalPrice;
    private final LocalDateTime reservationDate;

    @Builder
    private ReservationResponse(Long reservationId, String boardTitle, LocalDate scheduleDate, int quantity, BigDecimal totalPrice, LocalDateTime reservationDate) {
        this.reservationId = reservationId;
        this.boardTitle = boardTitle;
        this.scheduleDate = scheduleDate;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.reservationDate = reservationDate;
    }

    public static ReservationResponse from(Reservation reservation) {
        BigDecimal unitPrice = reservation.getSchedule().getPrice();
        int quantity = reservation.getQuantity();

        return ReservationResponse.builder()
                .reservationId(reservation.getId())
                .boardTitle(reservation.getSchedule().getBoard().getTitle())
                .scheduleDate(reservation.getSchedule().getScheduleDate())
                .quantity(quantity)
                .totalPrice(unitPrice.multiply(new BigDecimal(quantity)))
                .reservationDate(reservation.getCreatedAt())
                .build();
    }
}
