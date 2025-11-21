package com.example.hello_friends.board.domain;

import com.example.hello_friends.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishListRepository extends JpaRepository<WishList, Long> {
    // 사용자와 게시글로 찜 내역 찾기
    Optional<WishList> findByUserAndBoard(User user, Board board);

    // 내가 찜한 목록 확인
    @Query("select w from WishList w join fetch w.board where w.user.id = :userId")
    List<WishList> findAllByUserId(@Param("userId") Long userId);

    // 내 찜 목록 중에서 + 특정 위치 반경 N km 이내인 것만 조회
    @Query(value = "SELECT w.* FROM wishlist w " +
            "INNER JOIN board b ON w.board_id = b.board_id " +
            "WHERE w.user_id = :userId " +
            "AND (6371 * acos(cos(radians(:latitude)) * cos(radians(b.latitude)) " +
            "* cos(radians(b.longitude) - radians(:longitude)) " +
            "+ sin(radians(:latitude)) * sin(radians(b.latitude)))) <= :radius",
            nativeQuery = true)
    List<WishList> findNearbyWishLists(@Param("userId") Long userId,
                                       @Param("latitude") Double latitude,
                                       @Param("longitude") Double longitude,
                                       @Param("radius") Double radius);
}
