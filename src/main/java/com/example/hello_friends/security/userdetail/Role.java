package com.example.hello_friends.security.userdetail;

import lombok.Getter;

@Getter
public enum Role {
    ADMIN("ROLE_ADMIN"), CUSTOMER("ROLE_CUSTOMER"), SELLER("ROLE_SELLER");

    Role(String roleName) {
        this.roleName = roleName;
    }

    private  final String roleName;

}
