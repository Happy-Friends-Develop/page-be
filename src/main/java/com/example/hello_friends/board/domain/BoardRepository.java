package com.example.hello_friends.board.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findAllByBoardType(BoardType boardType);

    @Query(value = "SELECT *, " +
            "(6371 * acos(cos(radians(:centerLat)) * cos(radians(b.latitude)) * cos(radians(b.longitude) - radians(:centerLon)) + sin(radians(:centerLat)) * sin(radians(b.latitude)))) AS distance " +
            "FROM board b " +
            "HAVING distance <= :radius " +
            "ORDER BY distance", nativeQuery = true)
    List<Board> findBoardsNearBy(@Param("centerLat") Double centerLat,
                                 @Param("centerLon") Double centerLon,
                                 @Param("radius") Double radius);
}
