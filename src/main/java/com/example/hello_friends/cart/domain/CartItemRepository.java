package com.example.hello_friends.cart.domain;

import com.example.hello_friends.schedule.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    // 장바구니와 게시글 정보로 특정 아이템을 찾는 기능 (중복 확인용)
    Optional<CartItem> findByCartAndSchedule(Cart cart, Schedule schedule);
}
