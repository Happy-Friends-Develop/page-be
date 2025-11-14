package com.example.hello_friends.cart.application.response;

import com.example.hello_friends.cart.domain.CartItem;
import lombok.Builder;
import lombok.Data;

@Data
public class CartItemResponse {
    private final Long cartItemId;
    private final Long scheduleId;
    private final String boardTitle;
    private final int quantity;

    @Builder
    private CartItemResponse(Long cartItemId, Long scheduleId, String boardTitle, int quantity) {
        this.cartItemId = cartItemId;
        this.scheduleId = scheduleId;
        this.boardTitle = boardTitle;
        this.quantity = quantity;
    }

    public static CartItemResponse from(CartItem cartItem) {
        return CartItemResponse.builder()
                .cartItemId(cartItem.getId())
                .scheduleId(cartItem.getSchedule().getId())
                .boardTitle(cartItem.getSchedule().getBoard().getTitle())
                .quantity(cartItem.getQuantity())
                .build();
    }
}