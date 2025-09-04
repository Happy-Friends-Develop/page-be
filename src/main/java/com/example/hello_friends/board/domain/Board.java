package com.example.hello_friends.board.domain;

import com.example.hello_friends.common.entity.LogEntity;
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

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardFile> files = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardLike> likes = new ArrayList<>();

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

    // 총 좋아요 수
    public int getLikeCount() {
        return likes.size();
    }

    public void increaseView(){
        this.view += 1;
    }

    public Board(String title, String content, Long view){
        this.title = title;
        this.content = content;
        this.view = view;
    }

    public void update(String title, String content){
        this.title = title;
        this.content = content;
    }
}