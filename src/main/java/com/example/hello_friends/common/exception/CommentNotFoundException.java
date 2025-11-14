package com.example.hello_friends.common.exception;

import com.example.hello_friends.common.response.MotherException;

public class CommentNotFoundException extends MotherException {
    public CommentNotFoundException(String message) {
        super(message);
    }
}
