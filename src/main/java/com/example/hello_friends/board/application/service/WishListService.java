package com.example.hello_friends.board.application.service;

import com.example.hello_friends.board.application.response.WishListResponse;
import com.example.hello_friends.board.domain.Board;
import com.example.hello_friends.board.domain.BoardRepository;
import com.example.hello_friends.board.domain.WishLIstRepository;
import com.example.hello_friends.board.domain.WishList;
import com.example.hello_friends.board.infra.GeocodingService;
import com.example.hello_friends.common.exception.BoardNotFoundException;
import com.example.hello_friends.common.exception.UserNotFoundException;
import com.example.hello_friends.common.exception.WishListNotFoundException;
import com.example.hello_friends.user.domain.User;
import com.example.hello_friends.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishListService {
    private final WishLIstRepository wishLIstRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final GeocodingService geocodingService;

    // 찜하기
    @Transactional
    public void addWishList(Long userId, Long boardId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다. ID : " + userId));

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException("해당 게시물을 찾을 수 없습니다. ID : " + boardId));

        // WishList에 저장
        WishList wishList = new WishList(user, board);
        wishLIstRepository.save(wishList);

        // 해당 board 찜하기 개수 추가
        board.increaseWishlistCount();
    }

    // 찜 취소
    @Transactional
    public void deleteWishList(Long userId, Long boardId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다. ID : " + userId));

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException("해당 게시물을 찾을 수 없습니다. ID : " + boardId));

        WishList wishList = wishLIstRepository.findByUserAndBoard(user, board)
                .orElseThrow(() -> new WishListNotFoundException("찜한 적이 없어서 취소할 수 없습니다."));

        // db에서 삭제
        wishLIstRepository.delete(wishList);
        // board에서 찜하기 개수 줄이기
        board.decreaseWishlistCount();
    }

    // 찜 목록 확인(사용자 버전)
    @Transactional(readOnly = true)
    public List<WishListResponse> getMyWishList(Long userId){
        List<WishList> wishLists = wishLIstRepository.findAllByUserId(userId);

        return wishLists.stream()
                .map(WishListResponse::new)
                .collect(Collectors.toList());
    }

    // 선택한 주소 반경으로 조회되는 찜 목록
    @Transactional(readOnly = true)
    public List<WishListResponse> getMyNearbyWishList(Long userId, String currentAddress, Double radius) {
        Map<String, Double> myCoords = geocodingService.getCoordinate(currentAddress);

        if (myCoords == null) {
            throw new IllegalArgumentException("주소를 찾을 수 없습니다: " + currentAddress);
        }

        Double myLat = myCoords.get("latitude");
        Double myLon = myCoords.get("longitude");

        // 반경 설정 (입력 없으면 기본 10km)
        double searchRadius = (radius != null) ? radius : 10.0;

        List<WishList> nearbyWishLists = wishLIstRepository.findNearbyWishLists(userId, myLat, myLon, searchRadius);

        return nearbyWishLists.stream()
                .map(WishListResponse::new)
                .collect(Collectors.toList());
    }}
