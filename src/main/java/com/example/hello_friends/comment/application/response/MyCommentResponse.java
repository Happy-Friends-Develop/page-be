package com.example.hello_friends.comment.application.response;

import com.example.hello_friends.comment.domain.Comment;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MyCommentResponse {
    private Long commentId;
    // 내가 쓴 댓글 내용
    private String content;
    // 쓴 날짜
    private LocalDateTime createdAt;
    // 게시글 ID (클릭해서 이동용)
    private Long boardId;
    // 게시글 제목
    private String boardTitle;

    public MyCommentResponse(Comment comment) {
        this.commentId = comment.getId();
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedAt();

        // 댓글에 연결된 게시글 정보 꺼내기
        this.boardId = comment.getBoard().getId();
        this.boardTitle = comment.getBoard().getTitle();
    }
}
