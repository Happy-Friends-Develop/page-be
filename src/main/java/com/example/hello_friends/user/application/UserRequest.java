package com.example.hello_friends.user.application;

import lombok.Data;

@Data
public class UserRequest {
    private String id;

    private String password;

    private String name;

    private String nickname;

    private String phone;

    private String email;

    private String address;
}
