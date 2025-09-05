package com.example.hello_friends.user.application.response;

import com.example.hello_friends.user.domain.User;
import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String name;
    private String nickname;
    private String phone;
    private String email;
    private String address;

    public static UserResponse from(User user) {
        UserResponse responseDto = new UserResponse();
        responseDto.setId(user.getId());
        responseDto.setName(user.getName());
        responseDto.setNickname(user.getNickname());
        responseDto.setEmail(user.getEmail());
        responseDto.setAddress(user.getAddress());
        return responseDto;
    }
}
