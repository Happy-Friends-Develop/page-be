package com.example.hello_friends.board.application.service;

import com.example.hello_friends.board.application.request.BoardRequest;
import com.example.hello_friends.board.domain.Board;
import com.example.hello_friends.board.domain.BoardFile;
import com.example.hello_friends.board.domain.BoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final FileService fileService;
    private final VideoService videoService;

    // 보드 생성
    @Transactional
    public Board createBoard(BoardRequest request, List<MultipartFile> files) {
        Board board = new Board(request.getTitle(), request.getContent());

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

        return boardRepository.save(board);
    }
}