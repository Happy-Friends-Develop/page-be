package com.example.hello_friends.comment.presentation;

import com.example.hello_friends.comment.application.request.CommentRequest;
import com.example.hello_friends.comment.application.response.CommentResponse;
import com.example.hello_friends.comment.application.service.CommentService;
import com.example.hello_friends.comment.domain.Comment;
import com.example.hello_friends.common.response.Resp;
import com.example.hello_friends.security.annotation.Auth;
import com.example.hello_friends.security.filter.JwtPrincipalDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    // 댓글 생성
    @PostMapping("/api/user/boards/{boardId}/comments")
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable Long boardId,
            @RequestBody CommentRequest commentRequest,
            @Auth JwtPrincipalDto jwtPrincipalDto) {

        Comment comment = commentService.createComment(boardId, jwtPrincipalDto.getId(), commentRequest.getParentId(), commentRequest.getContent());

        return ResponseEntity.status(HttpStatus.CREATED).body(CommentResponse.from(comment));
    }

    // 특정 게시글의 댓글 목록 조회
    @GetMapping("/api/user/boards/{boardId}/comments")
    public Resp<List<CommentResponse>> getComments(@PathVariable Long boardId) {
        List<CommentResponse> comments = commentService.getCommentsByBoardId(boardId);
        return Resp.ok(comments);
    }

    // 댓글 수정
    @PatchMapping("/api/user/comments/{commentId}")
    public Resp<CommentResponse> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentRequest commentRequest,
            @Auth JwtPrincipalDto jwtPrincipalDto) {

        Comment updatedComment = commentService.updateComment(commentId, jwtPrincipalDto.getId(), commentRequest.getContent());

        return Resp.ok(CommentResponse.from(updatedComment));
    }

    // 댓글 삭제
    @DeleteMapping("/api/user/comments/{commentId}")
    public Resp<String> deleteComment(@PathVariable Long commentId, @Auth JwtPrincipalDto jwtPrincipalDto) {
        commentService.deleteComment(commentId, jwtPrincipalDto.getId());
        return Resp.ok("댓글 삭제 성공");
    }
}
