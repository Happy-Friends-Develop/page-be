package com.example.hello_friends.common.exception;

import com.example.hello_friends.common.response.MotherException;

public class UserNotFoundException extends MotherException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
