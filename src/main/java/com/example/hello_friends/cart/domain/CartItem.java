package com.example.hello_friends.cart.domain;

import com.example.hello_friends.common.entity.LogEntity;
import com.example.hello_friends.schedule.domain.Schedule;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartItem extends LogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    // 사용자가 담은 특정 날짜의 스케줄(상품 옵션)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    @Column(nullable = false)
    private int quantity; // 수량

    public CartItem(Cart cart, Schedule schedule, int quantity) {
        this.cart = cart;
        this.schedule = schedule;
        this.quantity = quantity;
    }

    // 수량 추가 메소드
    public void addQuantity(int quantity) {
        this.quantity += quantity;
    }
}
