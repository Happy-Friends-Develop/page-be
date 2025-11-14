package com.example.hello_friends.board.application.response;

import com.example.hello_friends.board.domain.Board;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BoardResponse {
    private Long id;
    private String title;
    private String content;
    private Long view;
    private String authorNickname; // 작성자 닉네임
    private int likeCount; // 좋아요 수
    private LocalDateTime createdAt;

    public static BoardResponse from(Board board) {
        BoardResponse responseDto = new BoardResponse();
        responseDto.setId(board.getId());
        responseDto.setTitle(board.getTitle());
        responseDto.setContent(board.getContent());
        responseDto.setView(board.getView());
        responseDto.setAuthorNickname(board.getUser().getNickname());
        responseDto.setCreatedAt(board.getCreatedAt());
        responseDto.setLikeCount(board.getLikes().size());
        return responseDto;
    }
}
