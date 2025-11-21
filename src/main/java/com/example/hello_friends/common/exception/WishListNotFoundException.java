package com.example.hello_friends.common.exception;

import com.example.hello_friends.common.response.MotherException;

public class WishListNotFoundException extends MotherException {
    public WishListNotFoundException(String message) {
        super(message);
    }
}
