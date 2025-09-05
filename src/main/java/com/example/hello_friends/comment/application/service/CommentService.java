package com.example.hello_friends.comment.application.service;

import com.example.hello_friends.board.domain.Board;
import com.example.hello_friends.board.domain.BoardRepository;
import com.example.hello_friends.comment.application.response.CommentResponse;
import com.example.hello_friends.comment.domain.Comment;
import com.example.hello_friends.comment.domain.CommentRepository;
import com.example.hello_friends.user.domain.User;
import com.example.hello_friends.user.domain.UserRepository;
import com.example.hello_friends.user.domain.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    // 댓글 및 대댓글 생성
    // parentId가 null이면 일반 댓글, 값이 있으면 대댓글로 생성
    @Transactional
    public Comment createComment(Long boardId, Long userId, Long parentId, String content) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Comment parent = null;
        // 대댓글인 경우
        if (parentId != null) {
            parent = commentRepository.findById(parentId)
                    .orElseThrow(() -> new IllegalArgumentException("부모 댓글을 찾을 수 없습니다."));
        }

        Comment comment = new Comment(content, board, user, parent);
        return commentRepository.save(comment);
    }

    // 댓글 수정
    @Transactional
    public Comment updateComment(Long commentId, Long userId, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        // 본인 댓글인지 확인
        if (!comment.getUser().getId().equals(userId)) {
            try {
                throw new AccessDeniedException("댓글을 수정할 권한이 없습니다.");
            } catch (AccessDeniedException e) {
                throw new RuntimeException(e);
            }
        }

        comment.update(content);
        return comment;
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));

        // 권한을 확인
        boolean isAuthor = comment.getUser().getId().equals(userId);
        boolean isAdmin = currentUser.getUserRole() == UserRole.ADMIN;

        // 권한밖일 경우 예외처리
        if (!isAuthor && !isAdmin) {
            try {
                throw new AccessDeniedException("댓글을 삭제할 권한이 없습니다.");
            } catch (AccessDeniedException e) {
                throw new RuntimeException(e);
            }
        }

        if (!comment.getChildren().isEmpty()) {
            // 대댓글이 있으면, 상태만 변경
            comment.delete();
        } else {
            // 대댓글이 없으면, DB에서 완전히 삭제
            commentRepository.delete(comment);
        }
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByBoardId(Long boardId) {
        // 모든 댓글 조회
        List<Comment> comments = commentRepository.findByBoardIdOrderByCreatedAtAsc(boardId);

        // DTO로 반환 및 계층 구조로 재조립
        return buildCommentHierarchy(comments);
    }

    // 댓글 목록을 계층 구조로 재조립 메서드
    private List<CommentResponse> buildCommentHierarchy(List<Comment> comments) {
        List<CommentResponse> topLevelComments = new ArrayList<>();
        Map<Long, CommentResponse> commentDtoMap = new HashMap<>();

        // 모든 댓글을 DTO로 변환 및 Map에 저장
        comments.forEach(comment -> {
            CommentResponse dto = CommentResponse.from(comment);
            commentDtoMap.put(dto.getId(), dto);
        });

        // 대댓글을 부모 댓글의 자식 리스트에 추가
        comments.forEach(comment -> {
            if (comment.getParent() != null) {
                CommentResponse parentDto = commentDtoMap.get(comment.getParent().getId());
                CommentResponse childDto = commentDtoMap.get(comment.getId());
                // 부모가 삭제되지 않은 경우
                if (parentDto != null) {
                    parentDto.getChildren().add(childDto);
                }
            }
        });

        // 최상위 댓글 (부모가 없는 댓글)만 리스트에 담아 반환
        comments.forEach(comment -> {
            if (comment.getParent() == null) {
                topLevelComments.add(commentDtoMap.get(comment.getId()));
            }
        });

        return topLevelComments;
    }
}
