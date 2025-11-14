package com.example.hello_friends.common.exception;

import com.example.hello_friends.common.response.MotherException;

public class NoAuthorityException extends MotherException {
    public NoAuthorityException(String message) {
        super(message);
    }
}
