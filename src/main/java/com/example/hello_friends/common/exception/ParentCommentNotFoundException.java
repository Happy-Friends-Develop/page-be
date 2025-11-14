package com.example.hello_friends.common.exception;

import com.example.hello_friends.common.response.MotherException;

public class ParentCommentNotFoundException extends MotherException {
    public ParentCommentNotFoundException(String message) {
        super(message);
    }
}
