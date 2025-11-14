package com.example.hello_friends.board.domain;

import lombok.Getter;

@Getter
public enum FileType {
    IMAGE("이미지"),
    VIDEO("동영상");

    private final String description;

    FileType(String description) {
        this.description = description;
    }
}
