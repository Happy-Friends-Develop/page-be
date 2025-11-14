package com.example.hello_friends.schedule.application.response;

import com.example.hello_friends.schedule.domain.Schedule;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ScheduleResponse {
    private final Long id;
    private final LocalDate scheduleDate;
    private final BigDecimal price;
    private final int maxHeadcount;
    private final int currentHeadcount;
    private final int remainingHeadcount;
    private final boolean isSoldOut;

    @Builder
    private ScheduleResponse(Long id, LocalDate scheduleDate, BigDecimal price, int maxHeadcount, int currentHeadcount) {
        this.id = id;
        this.scheduleDate = scheduleDate;
        this.price = price;
        this.maxHeadcount = maxHeadcount;
        this.currentHeadcount = currentHeadcount;
        this.remainingHeadcount = maxHeadcount - currentHeadcount;
        this.isSoldOut = this.remainingHeadcount <= 0;
    }

    public static ScheduleResponse from(Schedule schedule) {
        return ScheduleResponse.builder()
                .id(schedule.getId())
                .scheduleDate(schedule.getScheduleDate())
                .price(schedule.getPrice())
                .maxHeadcount(schedule.getMaxHeadcount())
                .currentHeadcount(schedule.getCurrentHeadcount())
                .build();
    }
}
