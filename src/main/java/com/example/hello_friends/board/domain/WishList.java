package com.example.hello_friends.board.domain;

import com.example.hello_friends.common.entity.LogEntity;
import com.example.hello_friends.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "wishlist", uniqueConstraints = {
        // 한 사람이 같은 글을 두 번 찜하지 못하게 방지
        @UniqueConstraint(columnNames = {"user_id", "board_id"})
})
public class WishList extends LogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wishlist_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    public WishList(User user, Board board) {
        this.user = user;
        this.board = board;
    }
}
