package com.example.hello_friends.common.exception;

import com.example.hello_friends.common.response.MotherException;

public class ReportNotFoundException extends MotherException {
    public ReportNotFoundException(String message) {
        super(message);
    }
}
