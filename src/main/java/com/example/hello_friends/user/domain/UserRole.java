package com.example.hello_friends.user.domain;

import com.example.hello_friends.security.userdetail.Role;


public enum UserRole {
    ADMIN, CUSTOMER, SELLER;


    public Role toRole() {
        return switch (this) {
            case ADMIN -> Role.ADMIN;
            case CUSTOMER -> Role.CUSTOMER;
            case SELLER -> Role.SELLER;
            default -> throw new IllegalArgumentException("No corresponding Role for UserRole: " + this);
        };
    }
}
