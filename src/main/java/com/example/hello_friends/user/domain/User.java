package com.example.hello_friends.user.domain;

import com.example.hello_friends.board.domain.Board;
import com.example.hello_friends.board.domain.BoardLike;
import com.example.hello_friends.common.entity.EntityState;
import com.example.hello_friends.common.entity.LogEntity;
import com.example.hello_friends.report.domain.Report;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "user")
public class User extends LogEntity {
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

    @JsonManagedReference
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Board> boards = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardLike> likes = new ArrayList<>();

    // 내가 한 신고 목록
    @JsonManagedReference("reporter-reports")
    @OneToMany(mappedBy = "reporter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Report> filedReports = new ArrayList<>();

    // 내가 받은 신고 목록
    @JsonManagedReference("reported-reports")
    @OneToMany(mappedBy = "reportedUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Report> receivedReports = new ArrayList<>();


    public User(String name, String nickname, String phone, String email, String address, Long authId) {
        super();
        this.name = name;
        this.nickname = nickname;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.userRole = UserRole.CUSTOMER;
        this.state = EntityState.ACTIVE;
        this.authId = authId;
    }

    public void update(String name, String nickname, String phone, String email, String address){
        this.name = name;
        this.nickname = nickname;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }

    public void delete(){
        this.state = EntityState.DELETE;
    }

    public void activate(){
        this.state = EntityState.ACTIVE;
    }
}
