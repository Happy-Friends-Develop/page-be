package com.example.hello_friends.schedule.application.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReservationRequest {
    private Long scheduleId;

    private int quantity;
}
