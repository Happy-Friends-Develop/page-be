package com.example.hello_friends.board.presentation;

import com.example.hello_friends.board.application.request.BoardRequest;
import com.example.hello_friends.board.application.service.BoardService;
import com.example.hello_friends.board.domain.Board;
import com.example.hello_friends.common.response.Resp;
import com.example.hello_friends.security.annotation.Auth;
import com.example.hello_friends.security.filter.JwtPrincipalDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "게시글")
public class BoardController {
    private final BoardService boardService;

    @Operation(summary = "게시판 추가", description = "게시판을 추가합니다. 파일 업로드도 가능합니다.")
    @PostMapping(value = "/api/user/board", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Board createBoard(
            @RequestPart(value = "request") BoardRequest boardRequest,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        return boardService.createBoard(boardRequest, files);
    }

    @Operation(summary = "게시글 단건 조회", description = "게시글 ID로 특정 게시글을 조회합니다. 조회수가 증가합니다.")
    @GetMapping("/api/board/{boardId}")
    public Board readBoard(
            @PathVariable Long boardId,
            HttpServletRequest request,
            HttpServletResponse response) {
        // 쿠키를 이용해 조회수를 증가시키는 로직을 먼저 호출
        boardService.updateView(boardId, request, response);
        return boardService.readBoard(boardId);
    }

    @Operation(summary = "게시글 목록 조회", description = "모든 게시글 목록을 조회합니다.")
    @GetMapping("/api/board")
    public List<Board> readBoardList() {
        return boardService.readBoardList();
    }

    @Operation(summary = "게시글 수정", description = "게시글 ID로 특정 게시글의 제목과 내용을 수정합니다.")
    @PutMapping("/api/user/board/{boardId}")
    public Board updateBoard(
            @PathVariable Long boardId,
            @RequestBody BoardRequest boardRequest) {
        return boardService.updateBoard(boardId, boardRequest);
    }

    @Operation(summary = "게시글 삭제", description = "게시글 ID로 특정 게시글을 삭제합니다.")
    @DeleteMapping("/api/user/board/{boardId}")
    public Resp<String> deleteBoard(@PathVariable Long boardId) {
        boardService.deleteBoard(boardId);
        return Resp.ok("게시글 삭제 성공");
    }

    @Operation(summary = "게시글 좋아요 토글", description = "게시글에 대한 좋아요를 추가하거나 취소합니다.")
    @PostMapping("/api/user/board/{boardId}/like")
    public ResponseEntity<Void> toggleLike(
            @PathVariable Long boardId,
            @Parameter(hidden = true) @Auth JwtPrincipalDto jwtprincipalDto
    ) {
        boardService.toggleLike(boardId, jwtprincipalDto.getId());
        return ResponseEntity.ok().build();
    }
}