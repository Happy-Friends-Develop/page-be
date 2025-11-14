package com.example.hello_friends.cart.application.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CartRequest {
    private Long scheduleId;
    private int quantity;
}
