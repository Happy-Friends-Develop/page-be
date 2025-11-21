package com.example.hello_friends.comment.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByBoardIdOrderByCreatedAtAsc(Long boardId);

    // 내 댓글 + 게시글 정보 한 방에 가져오기 (최신순 정렬)
    @Query("select c from Comment c join fetch c.board where c.user.id = :userId order by c.createdAt desc")
    List<Comment> findAllByUserId(@Param("userId") Long userId);
}
