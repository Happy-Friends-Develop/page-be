package com.example.hello_friends.common.exception;

import com.example.hello_friends.common.response.MotherException;

public class EventNotFoundException extends MotherException {
    public EventNotFoundException(String message) {
        super(message);
    }
}
