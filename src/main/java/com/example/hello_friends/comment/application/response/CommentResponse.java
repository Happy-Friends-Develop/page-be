package com.example.hello_friends.comment.application.response;

import com.example.hello_friends.comment.domain.Comment;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class CommentResponse {
    private Long id;
    private String content;
    private String authorNickname;
    private LocalDateTime createdAt;

    private List<CommentResponse> children = new ArrayList<>();

    public static CommentResponse from(Comment comment) {
        CommentResponse dto = new CommentResponse();
        dto.setId(comment.getId());
        dto.setAuthorNickname(comment.getUser().getNickname());
        dto.setCreatedAt(comment.getCreatedAt());

        if (comment.isDeleted()) {
            dto.setContent("삭제된 댓글입니다.");
        } else {
            dto.setContent(comment.getContent());
        }

        return dto;
    }
}
