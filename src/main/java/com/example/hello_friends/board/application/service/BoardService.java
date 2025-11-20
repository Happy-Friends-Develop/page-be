package com.example.hello_friends.board.application.service;

import com.example.hello_friends.board.application.request.BoardRequest;
import com.example.hello_friends.board.application.response.BoardResponse;
import com.example.hello_friends.board.domain.*;
import com.example.hello_friends.board.infra.GeocodingService;
import com.example.hello_friends.common.entity.EntityState;
import com.example.hello_friends.common.exception.BoardNotFoundException;
import com.example.hello_friends.common.exception.NoAuthorityException;
import com.example.hello_friends.common.exception.UserNotFoundException;
import com.example.hello_friends.notification.application.service.NotificationService;
import com.example.hello_friends.user.domain.User;
import com.example.hello_friends.user.domain.UserRepository;
import com.example.hello_friends.user.domain.UserRole;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final FileService fileService;
    private final VideoService videoService;
    private final UserRepository userRepository;
    private final BoardLikeRepository boardLikeRepository;
    private final NotificationService notificationService;
    private final GeocodingService geocodingService;

    // 보드 생성
    @Transactional
    public BoardResponse createBoard(BoardRequest request, List<MultipartFile> files, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("해당하는 사용자를 찾을 수 없습니다. ID : " + userId));

        Board board = new Board(request.getTitle(), request.getContent(), user, request.getBoardType());

        // 주소 -> 좌표로 변환
        if (request.getAddress() != null && !request.getAddress().isEmpty()) {
            Map<String, Double> coords = geocodingService.getCoordinate(request.getAddress());
            if (coords != null) {
                // 좌표 저장
                board.updateLocation(request.getAddress(), coords.get("latitude"), coords.get("longitude"));
            } else {
                // 좌표를 못 찾았으면 주소만 저장
                board.updateLocation(request.getAddress(), null, null);
            }
        }

        // 파일 처리
        if (files != null && !files.isEmpty()) {
            for (int i = 0; i < files.size(); i++) {
                MultipartFile file = files.get(i);
                String mimeType = file.getContentType();
                BoardFile boardFile;

                if (mimeType != null && mimeType.startsWith("image/")) {
                    boardFile = fileService.uploadImageAndRequestProcessing(file, mimeType, i);
                } else if (mimeType != null && mimeType.startsWith("video/")) {
                    boardFile = videoService.uploadVideoAndRequestProcessing(file, mimeType, i);
                } else {
                    log.warn("지원하지 않는 파일 타입입니다: {}", mimeType);
                    continue;
                }

                board.addFile(boardFile);
            }
        }

        Board savedBoard = boardRepository.save(board);

        return BoardResponse.from(savedBoard);
    }

    @Transactional(readOnly = true)
    public BoardResponse findBoardById(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new BoardNotFoundException("해당 게시글이 없습니다. id=" + id));

        return BoardResponse.fromDetail(board);
    }

    // 게시글 목록 조회
    @Transactional(readOnly = true)
    public List<BoardResponse> readBoardList(BoardType boardType){
        List<Board> boardList;
        if(boardType == null){
            boardList = boardRepository.findAll();
        } else {
            boardList = boardRepository.findAllByBoardType(boardType);
        }
        return boardList.stream()
                .map(BoardResponse::from)
                .toList();
    }

    // 선택한 주소 반경으로 조회
    @Transactional(readOnly = true)
    public List<BoardResponse> findNearbyBoards(String myAddress, Double radius) {
        // 내 주소 -> 좌표 변환
        Map<String, Double> myCoords = geocodingService.getCoordinate(myAddress);

        if (myCoords == null) {
            throw new IllegalArgumentException("주소를 찾을 수 없습니다: " + myAddress);
        }

        Double myLat = myCoords.get("latitude");
        Double myLon = myCoords.get("longitude");

        // 전달받은 radius(km) 만큼 조회
        // radius가 null인 경우 10km로 설정
        double searchRadius = (radius != null) ? radius : 10.0;

        List<Board> nearbyBoards = boardRepository.findBoardsNearBy(myLat, myLon, searchRadius);

        return nearbyBoards.stream()
                .map(BoardResponse::from)
                .toList();
    }

    // 게시글 수정
    @Transactional
    public BoardResponse updateBoard(Long boardId, BoardRequest boardRequest, Long currentUserId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException("해당 게시글이 존재하지 않습니다. ID: " + boardId));

        if (!board.getUser().getId().equals(currentUserId)) {
            throw new NoAuthorityException("게시글을 수정할 권한이 없습니다.");
        }

        // 주소 수정 시 좌표 계산
        if (boardRequest.getAddress() != null) {
            Map<String, Double> coords = geocodingService.getCoordinate(boardRequest.getAddress());
            if(coords != null){
                board.updateLocation(boardRequest.getAddress(), coords.get("latitude"), coords.get("longitude"));
            } else {
                board.updateLocation(boardRequest.getAddress(), null, null);
            }
        }

        // 제목, 내용, 타입 수정
        board.update(boardRequest.getTitle(), boardRequest.getContent(), boardRequest.getBoardType());

        return BoardResponse.from(board);
    }

    // 게시글 삭제
    @Transactional
    public void deleteBoard(Long boardId, Long currentUserId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException("해당 게시글이 존재하지 않습니다. ID: " + boardId));

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new UserNotFoundException("사용자 정보를 찾을 수 없습니다. ID: " + currentUserId));

        boolean isAuthor = board.getUser().getId().equals(currentUserId);
        boolean isAdmin = currentUser.getUserRole() == UserRole.ADMIN;

        if (!isAuthor && !isAdmin) {
            throw new NoAuthorityException("게시글을 삭제할 권한이 없습니다.");
        }

        if (isAdmin && !isAuthor) {
            User boardAuthor = board.getUser();
            String notificationContent = "회원님의 게시글이 관리자에 의해 삭제되었습니다. 자세한 내용은 고객센터에 문의해주세요.";
            String notificationUrl = "/my-page/posts";

            notificationService.send(boardAuthor, notificationContent, notificationUrl);
        }

        boardRepository.delete(board);
    }

    // 게시글 좋아요
    @Transactional
    public void toggleLike(Long boardId, Long userId){
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException("해당 게시글이 존재하지 않습니다."));

        User user = userRepository.findByIdAndState(userId, EntityState.ACTIVE)
                .orElseThrow(() -> new UserNotFoundException("ID " + userId + "에 해당하는 사용자를 찾을 수 없음"));

        Optional<BoardLike> boardLike = boardLikeRepository.findByUserAndBoard(user, board);

        if (boardLike.isPresent()) {
            boardLikeRepository.delete(boardLike.get());
        } else {
            boardLikeRepository.save(new BoardLike(user, board));

            User boardAuthor = board.getUser();

            if (!boardAuthor.getId().equals(userId)) {
                String notificationContent = user.getNickname() + "님이 회원님의 게시글을 좋아합니다.";
                String notificationUrl = "/boards/" + boardId;
                notificationService.send(boardAuthor, notificationContent, notificationUrl);
            }
        }
    }

    // 조회수 상승
    @Transactional
    public void updateView(Long boardId, HttpServletRequest request, HttpServletResponse response) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException("게시글을 찾을 수 없습니다."));

        Cookie oldCookie = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("postView")) {
                    oldCookie = cookie;
                }
            }
        }

        if (oldCookie != null) {
            if (!oldCookie.getValue().contains("[" + boardId.toString() + "]")) {
                board.increaseView();
                oldCookie.setValue(oldCookie.getValue() + "_[" + boardId + "]");
                oldCookie.setPath("/");
                oldCookie.setMaxAge(60 * 60 * 24);
                response.addCookie(oldCookie);
            }
        } else {
            board.increaseView();
            Cookie newCookie = new Cookie("postView", "[" + boardId + "]");
            newCookie.setPath("/");
            newCookie.setMaxAge(60 * 60 * 24);
            response.addCookie(newCookie);
        }
    }
}