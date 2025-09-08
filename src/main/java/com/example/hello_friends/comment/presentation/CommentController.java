package com.example.hello_friends.comment.presentation;

import com.example.hello_friends.comment.application.request.CommentRequest;
import com.example.hello_friends.comment.application.response.CommentResponse;
import com.example.hello_friends.comment.application.service.CommentService;
import com.example.hello_friends.comment.domain.Comment;
import com.example.hello_friends.common.response.Resp;
import com.example.hello_friends.security.annotation.Auth;
import com.example.hello_friends.security.filter.JwtPrincipalDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "댓글")
public class CommentController {
    private final CommentService commentService;

    // 댓글 생성
    @Operation(summary = "댓글을 생성합니다.", description = "댓글 및 대댓글까지 생성 가능합니다.")
    @PostMapping("/api/user/boards/{boardId}/comments")
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable Long boardId,
            @RequestBody CommentRequest commentRequest,
            @Parameter(hidden = true) @Auth JwtPrincipalDto jwtPrincipalDto) {

        Comment comment = commentService.createComment(boardId, jwtPrincipalDto.getId(), commentRequest.getParentId(), commentRequest.getContent());

        return ResponseEntity.status(HttpStatus.CREATED).body(CommentResponse.from(comment));
    }

    // 특정 게시글의 댓글 목록 조회
    @Operation(summary = "게시글에 작성된 댓글을 조회할 수 있습니다.", description = "게시글에 매핑된 댓글을 조회합니다.")
    @GetMapping("/api/user/boards/{boardId}/comments")
    public Resp<List<CommentResponse>> getComments(@PathVariable Long boardId) {
        List<CommentResponse> comments = commentService.getCommentsByBoardId(boardId);
        return Resp.ok(comments);
    }

    // 댓글 수정
    @Operation(summary = "댓글을 수정합니다.", description = "댓글을 작성한 사람만 수정이 가능합니다.")
    @PatchMapping("/api/user/comments/{commentId}")
    public Resp<CommentResponse> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentRequest commentRequest,
            @Parameter(hidden = true) @Auth JwtPrincipalDto jwtPrincipalDto) {

        Comment updatedComment = commentService.updateComment(commentId, jwtPrincipalDto.getId(), commentRequest.getContent());

        return Resp.ok(CommentResponse.from(updatedComment));
    }

    // 댓글 삭제
    @Operation(summary = "댓글을 삭제합니다.", description = "댓글을 작성한 사람 및 관리자만 삭제가 가능합니다.")
    @DeleteMapping("/api/user/comments/{commentId}")
    public Resp<String> deleteComment(@PathVariable Long commentId, @Parameter(hidden = true) @Auth JwtPrincipalDto jwtPrincipalDto) {
        commentService.deleteComment(commentId, jwtPrincipalDto.getId());
        return Resp.ok("댓글 삭제 성공");
    }
}
