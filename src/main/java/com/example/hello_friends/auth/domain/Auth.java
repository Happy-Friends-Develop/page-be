package com.example.hello_friends.auth.domain;

import com.example.hello_friends.common.entity.LogEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
// 매개변수 없는 기본 생성자
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
// 고유 제약 조건 설정
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"loginId"})})
public class Auth extends LogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
