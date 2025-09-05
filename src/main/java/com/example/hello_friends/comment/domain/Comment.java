package com.example.hello_friends.comment.domain;

import com.example.hello_friends.board.domain.Board;
import com.example.hello_friends.common.entity.LogEntity;
import com.example.hello_friends.user.domain.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "comment")
public class Comment extends LogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @Column(nullable = false, length = 1000)
    private String content;

    // '삭제된 댓글입니다' 상태를 표시
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 대댓글용 (부모 댓글)
    // 일반 댓글 값이 null
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    // 자식 댓글(대댓글) 목록
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> children = new ArrayList<>();


    public Comment(String content, Board board, User user, Comment parent) {
        this.content = content;
        this.board = board;
        this.user = user;
        this.parent = parent;
        this.isDeleted = false; // 처음엔 삭제되지 않은 상태
    }

    // 댓글 내용 수정
    public void update(String content) {
        this.content = content;
    }

    // 댓글 삭제 (대댓글이 있으면 내용을 바꾸고, 없으면 DB에서 진짜 삭제)
    public void delete() {
        this.isDeleted = true;
        this.content = "삭제된 댓글입니다.";
    }
}