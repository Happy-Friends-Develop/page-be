package com.example.hello_friends.board.presentation;

import com.example.hello_friends.board.application.request.BoardRequest;
import com.example.hello_friends.board.application.service.BoardService;
import com.example.hello_friends.board.domain.Board;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    @Operation(summary = "게시판 추가", description = "게시판을 추가합니다. 파일 업로드도 가능합니다.")
    @PostMapping("/api/user/board")
    public Board createBoard(
            @RequestPart(value = "request") BoardRequest boardRequest,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        return boardService.createBoard(boardRequest, files);
    }
}