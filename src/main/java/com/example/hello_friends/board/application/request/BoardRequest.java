package com.example.hello_friends.board.application.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardRequest {
    private String title;       // 게시글 제목
    private String content;     // 게시글 내용
    private Long view;
}
