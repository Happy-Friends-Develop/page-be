package com.example.hello_friends.common.exception;

import com.example.hello_friends.common.response.MotherException;

public class NotificationNotFoundException extends MotherException {
    public NotificationNotFoundException(String message) {
        super(message);
    }
}
