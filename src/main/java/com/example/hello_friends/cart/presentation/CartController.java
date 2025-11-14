package com.example.hello_friends.cart.presentation;

import com.example.hello_friends.cart.application.request.CartRequest;
import com.example.hello_friends.cart.application.response.CartResponse;
import com.example.hello_friends.cart.application.service.CartService;
import com.example.hello_friends.common.response.Resp;
import com.example.hello_friends.security.annotation.Auth;
import com.example.hello_friends.security.filter.JwtPrincipalDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "장바구니")
public class CartController {
    private final CartService cartService;

    @Operation(summary = "장바구니에 아이템 추가", description = "특정 게시글(상품)을 지정된 수량만큼 장바구니에 추가합니다.")
    @PostMapping("/api/user/items")
    public ResponseEntity<Resp<String>> addBoardToCart(
            @Parameter(hidden = true) @Auth JwtPrincipalDto jwtPrincipalDto,
            @RequestBody CartRequest cartRequest
    ) {
        cartService.addBoardToCart(jwtPrincipalDto.getId(), cartRequest);
        return ResponseEntity.ok(Resp.ok("장바구니에 아이템을 추가했습니다."));
    }

    @Operation(summary = "내 장바구니 조회", description = "현재 로그인한 사용자의 장바구니에 담긴 모든 아이템 목록을 조회합니다.")
    @GetMapping("/api/user/cartlist")
    public ResponseEntity<Resp<CartResponse>> getMyCart(
            @Parameter(hidden = true) @Auth JwtPrincipalDto jwtPrincipalDto
    ) {
        CartResponse cartResponse = cartService.getCartForUser(jwtPrincipalDto.getId());
        return ResponseEntity.ok(Resp.ok(cartResponse));
    }

    @Operation(summary = "장바구니 아이템 삭제", description = "장바구니에서 특정 아이템을 삭제합니다.")
    @DeleteMapping("/api/user/items/{cartItemId}")
    public ResponseEntity<Resp<String>> removeCartItem(
            @Parameter(hidden = true) @Auth JwtPrincipalDto jwtPrincipalDto,
            @Parameter(description = "삭제할 장바구니 아이템의 ID")
            @PathVariable Long cartItemId
    ) {
        cartService.removeCartItem(jwtPrincipalDto.getId(), cartItemId);
        return ResponseEntity.ok(Resp.ok("장바구니에서 아이템을 삭제했습니다."));
    }
}
