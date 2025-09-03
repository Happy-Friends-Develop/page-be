package com.example.hello_friends.common.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EntityState {
    ACTIVE(true), DELETE(false);

    private final boolean show;
}
