package com.example.hello_friends.board.domain;

import com.example.hello_friends.user.domain.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {
    Optional<BoardLike> findByUserAndBoard(User user, Board board);
    void deleteByBoard(Board board);

    @Query("select bl from BoardLike bl join fetch bl.board where bl.user.id = :userId")
    List<BoardLike> findAllByUserId(@Param("userId") Long userId);
}
