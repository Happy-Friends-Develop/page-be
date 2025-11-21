package com.example.hello_friends.board.application.response;

import com.example.hello_friends.board.domain.WishList;
import lombok.Getter;

@Getter
public class WishListResponse {
    private final Long wishListId;
    private final Long boardId;
    private final String title;
    private final String address;

    public WishListResponse(WishList wishlist) {
        this.wishListId = wishlist.getId();
        this.boardId = wishlist.getBoard().getId();
        this.title = wishlist.getBoard().getTitle();
        this.address = wishlist.getBoard().getAddress();
    }
}