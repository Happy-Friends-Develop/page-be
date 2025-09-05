package com.example.hello_friends.board.application.service;

import com.example.hello_friends.board.application.request.BoardRequest;
import com.example.hello_friends.board.application.response.BoardResponse;
import com.example.hello_friends.board.domain.*;
import com.example.hello_friends.common.entity.EntityState;
import com.example.hello_friends.user.domain.User;
import com.example.hello_friends.user.domain.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;
import java.util.List;
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

    // 보드 생성
    @Transactional
    public BoardResponse createBoard(BoardRequest request, List<MultipartFile> files, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("ID " + userId + "에 해당하는 사용자를 찾을 수 없음"));

        Board board = new Board(request.getTitle(), request.getContent(), user);

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
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + id));

        return BoardResponse.from(board);

    }

    // 게시글 목록 조회
    @Transactional(readOnly = true)
    public List<BoardResponse> readBoardList(){
        List<Board> boardList = boardRepository.findAll();

        return boardList.stream()
                .map(BoardResponse::from)
                .toList();
    }

    // 게시글 수정
    @Transactional
    public BoardResponse updateBoard(Long boardId, BoardRequest boardRequest, Long currentUserId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다. ID: " + boardId));

        // 게시글 작성자의 ID와 현재 요청한 사용자의 ID가 같은지 확인
        if (!board.getUser().getId().equals(currentUserId)) {
            try {
                throw new AccessDeniedException("게시글을 수정할 권한이 없습니다.");
            } catch (AccessDeniedException e) {
                throw new RuntimeException(e);
            }
        }

        board.update(boardRequest.getTitle(), boardRequest.getContent());

        return BoardResponse.from(board);
    }

    // 게시글 삭제
    @Transactional
    public void deleteBoard(Long id){
        boardRepository.deleteById(id);
    }

    // 게시글 좋아요
    @Transactional
    public void toggleLike(Long boardId, Long userId){
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        User user = userRepository.findByIdAndState(userId, EntityState.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("ID " + userId + "에 해당하는 사용자를 찾을 수 없음"));

        // 이미 좋아요를 눌렀는지 확인
        Optional<BoardLike> boardLike = boardLikeRepository.findByUserAndBoard(user, board);

        if (boardLike.isPresent()) {
            // 이미 눌렀다면 좋아요 취소
            boardLikeRepository.delete(boardLike.get());
        } else {
            // 누르지 않았다면 좋아요 추가
            boardLikeRepository.save(new BoardLike(user, board));
        }
    }

    @Transactional
    public void updateView(Long boardId, HttpServletRequest request, HttpServletResponse response) {
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

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
            // 기존 쿠키가 있을 경우
            if (!oldCookie.getValue().contains("[" + boardId.toString() + "]")) {
                // 해당 게시글 조회 기록이 없으면 조회수 올리고, 쿠키에 기록 추가
                board.increaseView();
                oldCookie.setValue(oldCookie.getValue() + "_[" + boardId + "]");
                oldCookie.setPath("/");
                oldCookie.setMaxAge(60 * 60 * 24); // 쿠키 유효시간: 24시간
                response.addCookie(oldCookie);
            }
        } else {
            // 기존 쿠키가 없을 경우
            board.increaseView();
            Cookie newCookie = new Cookie("postView", "[" + boardId + "]");
            newCookie.setPath("/");
            newCookie.setMaxAge(60 * 60 * 24);
            response.addCookie(newCookie);
        }
    }
}