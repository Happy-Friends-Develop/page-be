package com.example.hello_friends.comment.application.request;

import lombok.Data;

@Data
public class CommentRequest {
    private String content;
    private Long parentId;
}
