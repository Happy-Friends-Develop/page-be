package com.example.hello_friends.cart.application.response;

import com.example.hello_friends.cart.domain.Cart;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class CartResponse {
    private final Long cartId;
    private final List<CartItemResponse> items;

    @Builder
    private CartResponse(Long cartId, List<CartItemResponse> items) {
        this.cartId = cartId;
        this.items = items;
    }

    public static CartResponse from(Cart cart) {
        List<CartItemResponse> itemResponses = cart.getCartItems().stream()
                .map(CartItemResponse::from)
                .collect(Collectors.toList());

        return CartResponse.builder()
                .cartId(cart.getId())
                .items(itemResponses)
                .build();
    }
}