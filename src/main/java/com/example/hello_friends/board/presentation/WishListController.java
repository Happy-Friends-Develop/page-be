package com.example.hello_friends.board.presentation;

import com.example.hello_friends.board.application.response.WishListResponse;
import com.example.hello_friends.board.application.service.WishListService;
import com.example.hello_friends.common.response.Resp;
import com.example.hello_friends.security.annotation.Auth;
import com.example.hello_friends.security.filter.JwtPrincipalDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "찜하기", description = "게시글 찜하기 관련 API")
@RequestMapping("/api/user/wishlist")
public class WishListController {
    private final WishListService wishListService;

    @Operation(summary = "게시글 찜하기", description = "게시글 ID를 받아 찜 목록에 추가합니다.")
    @PostMapping("/{boardId}")
    public ResponseEntity<Resp<Void>> addWishList(
            @Parameter(hidden = true) @Auth JwtPrincipalDto jwtPrincipalDto,
            @PathVariable Long boardId
    ) {
        wishListService.addWishList(jwtPrincipalDto.getId(), boardId);
        return ResponseEntity.ok(Resp.ok(null));
    }

    @Operation(summary = "찜 취소하기", description = "게시글 ID를 받아 찜 목록에서 삭제합니다.")
    @DeleteMapping("/{boardId}")
    public ResponseEntity<Resp<Void>> deleteWishList(
            @Parameter(hidden = true) @Auth JwtPrincipalDto jwtPrincipalDto,
            @PathVariable Long boardId
    ) {
        wishListService.deleteWishList(jwtPrincipalDto.getId(), boardId);
        return ResponseEntity.ok(Resp.ok(null));
    }

    @Operation(summary = "내 찜 목록 조회", description = "내가 찜한 게시글 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<Resp<List<WishListResponse>>> getMyWishList(
            @Parameter(hidden = true) @Auth JwtPrincipalDto jwtPrincipalDto
    ) {
        List<WishListResponse> response = wishListService.getMyWishList(jwtPrincipalDto.getId());
        return ResponseEntity.ok(Resp.ok(response));
    }

    @Operation(summary = "내 주변 찜 목록 조회", description = "현재 위치(주소)를 기준으로 반경 내에 있는 찜 목록만 보여줍니다.")
    @GetMapping("/nearby")
    public ResponseEntity<Resp<List<WishListResponse>>> getMyNearbyWishList(
            @Parameter(hidden = true) @Auth JwtPrincipalDto jwtPrincipalDto,
            @RequestParam String address,
            @RequestParam(required = false) Double radius
    ) {
        List<WishListResponse> response = wishListService.getMyNearbyWishList(jwtPrincipalDto.getId(), address, radius);
        return ResponseEntity.ok(Resp.ok(response));
    }
}
