package com.example.hello_friends.common.exception;

import com.example.hello_friends.common.response.MotherException;

public class ReservationNotFoundException extends MotherException {
    public ReservationNotFoundException(String message) {
        super(message);
    }
}
