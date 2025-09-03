package com.example.hello_friends.user.domain;

import com.example.hello_friends.common.entity.EntityState;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private Long id;

    @Column(name ="user_name",nullable = false)
    private String name;

    @Column(name="user_nickname", nullable = false)
    private String nickname;

    @Column(name="user_phone", nullable = false)
    private String phone;

    @Column(name="user_email", nullable = false)
    private String email;

    @Column(name="user_address", nullable = false)
    private String address;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Column(name ="user_state",nullable = false)
    @Enumerated(EnumType.STRING)
    private EntityState state;

    @Column(nullable = false)
    private Long authId;


    public User(String name, String nickname, String phone, String email, String address, Long authId) {
        this.name = name;
        this.nickname = nickname;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.userRole = UserRole.CUSTOMER;
        this.state = EntityState.ACTIVE;
        this.authId = authId;
    }
}
