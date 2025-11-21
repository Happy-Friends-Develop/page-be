package com.example.hello_friends.board.domain;

import com.example.hello_friends.comment.domain.Comment;
import com.example.hello_friends.common.entity.LogEntity;
import com.example.hello_friends.schedule.domain.Schedule;
import com.example.hello_friends.user.domain.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
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
@Table(name = "board")
public class Board extends LogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    @Column(name = "board_title", columnDefinition = "TEXT")
    private String title;

    @Column(name = "board_content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "board_view")
    private Long view;

    @Column(name = "address")
    private String address;

    // 위도
    @Column(name = "latitude")
    private Double latitude;

    // 경도
    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "wishlist_count")
    private Long wishListCount = 0L;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BoardType boardType;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @JsonManagedReference
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardFile> files = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardLike> likes = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Schedule> schedules = new ArrayList<>();

    // 파일 추가 메서드
    public void addFile(BoardFile file) {
        this.files.add(file);
        file.setBoard(this);
    }

    // 파일 제거 메서드
    public void removeFile(BoardFile file) {
        this.files.remove(file);
        file.setBoard(null);
    }

    public void increaseView(){
        this.view += 1;
    }

    public Board(String title, String content, User user, BoardType boardType){
        this.title = title;
        this.content = content;
        this.view = 0L;
        this.boardType = boardType;
        this.user = user;
    }

    public void update(String title, String content, BoardType boardType){
        this.title = title;
        this.content = content;
        this.boardType = boardType;
    }

    public void updateLocation(String address, Double latitude, Double longitude) {
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // 찜하기
    public void increaseWishlistCount() {
        this.wishListCount++;
    }

    // 찜 취소
    public void decreaseWishlistCount() {
        if (this.wishListCount > 0) {
            this.wishListCount--;
        }
    }
}