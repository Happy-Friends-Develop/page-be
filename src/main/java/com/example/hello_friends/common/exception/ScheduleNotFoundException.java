package com.example.hello_friends.common.exception;

import com.example.hello_friends.common.response.MotherException;

public class ScheduleNotFoundException extends MotherException {
    public ScheduleNotFoundException(String message) {
        super(message);
    }
}
