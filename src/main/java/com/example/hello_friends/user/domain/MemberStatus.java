package com.example.hello_friends.user.domain;

import lombok.Getter;

@Getter
public enum MemberStatus {
    ACTIVE("활성"),   // 정상
    DORMANT("휴면"), // 휴면 (1년 미접속)
    WITHDRAWN("탈퇴"); // 탈퇴

    private final String description;

    MemberStatus(String description) {
        this.description = description;
    }
}
