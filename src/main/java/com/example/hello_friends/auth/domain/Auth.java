package com.example.hello_friends.auth.domain;

import com.example.hello_friends.common.entity.LogEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"loginId"})})
public class Auth extends LogEntity {
    @Id
    @SequenceGenerator(
            name = "auth_seq_gen",
            sequenceName = "auth_seq",
            initialValue = 1,
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "auth_seq_gen")
    @Column(name="auth_id")
    private Long id;

    @Column(nullable = false)
    private String loginId;

    @Column(nullable = false)
    private String pwd;

    public Auth(String loginId, String pwd) {
        this.loginId = loginId;
        this.pwd = pwd;
    }
}