package com.example.hello_friends.schedule.application.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class ScheduleUpdateRequest {
    private LocalDate scheduleDate;
    private int maxHeadcount;
    private BigDecimal price;
}
