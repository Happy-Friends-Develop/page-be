package com.example.hello_friends.common.exception;

import com.example.hello_friends.common.response.MotherException;

public class BoardNotFoundException extends MotherException {
    public BoardNotFoundException(String message) {
        super(message);
    }
}
