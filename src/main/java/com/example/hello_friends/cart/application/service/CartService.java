package com.example.hello_friends.cart.application.service;

import com.example.hello_friends.board.domain.BoardRepository;
import com.example.hello_friends.cart.application.request.CartRequest;
import com.example.hello_friends.cart.application.response.CartResponse;
import com.example.hello_friends.cart.domain.Cart;
import com.example.hello_friends.cart.domain.CartItem;
import com.example.hello_friends.cart.domain.CartItemRepository;
import com.example.hello_friends.cart.domain.CartRepository;
import com.example.hello_friends.schedule.domain.Schedule;
import com.example.hello_friends.schedule.domain.ScheduleRepository;
import com.example.hello_friends.user.domain.User;
import com.example.hello_friends.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final ScheduleRepository scheduleRepository;

    @Transactional
    public void addBoardToCart(Long userId, CartRequest cartRequest) {
        // 유저 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        // 게시글 확인
        Schedule schedule = scheduleRepository.findById(cartRequest.getScheduleId())
                .orElseThrow(() -> new IllegalArgumentException("게시글(상품)을 찾을 수 없습니다."));
        // 카트 확인
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(new Cart(user)));

        // 장바구니에 이미 동일한 상품이 담겨 있는지 확인
        Optional<CartItem> existingItem = cartItemRepository.findByCartAndSchedule(cart, schedule);

        if (existingItem.isPresent()) {
            CartItem cartItem = existingItem.get();
            cartItem.addQuantity(cartRequest.getQuantity());
        } else {
            // 상품이 존재하지 않으면, 새로운 CartItem을 생성해 장바구니에 추가
            CartItem newItem = new CartItem(cart, schedule, cartRequest.getQuantity());
            cartItemRepository.save(newItem);
        }
    }
    @Transactional(readOnly = true)
    public CartResponse getCartForUser(Long userId) {
        Optional<Cart> cartOptional = cartRepository.findByUserId(userId);

        if (cartOptional.isEmpty()) {
            // 장바구니가 없는 경우 비어있는 장바구니를 반환
            return CartResponse.builder()
                    .cartId(null)
                    .items(Collections.emptyList())
                    .build();
        }
        return CartResponse.from(cartOptional.get());
    }

    @Transactional
    public void removeCartItem(Long userId, Long cartItemId) {
        // 삭제할 장바구니 아이템을 ID로 찾습니다.
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("장바구니에 해당 아이템이 없습니다."));

        // 아이템이 현재 로그인한 사용자의 장바구니에 속한 것인지 확인
        if (!cartItem.getCart().getUser().getId().equals(userId)) {
            try {
                throw new AccessDeniedException("해당 아이템을 삭제할 권한이 없습니다.");
            } catch (AccessDeniedException e) {
                throw new RuntimeException(e);
            }
        }
        cartItemRepository.delete(cartItem);
    }
}
