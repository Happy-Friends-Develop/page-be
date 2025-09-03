package com.example.hello_friends.board.application.service;

import com.example.hello_friends.board.application.request.BoardRequest;
import com.example.hello_friends.board.application.request.FileInfo;
import com.example.hello_friends.board.application.request.VideoInfo;
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

                if (mimeType.startsWith("image/")) {
                    // 이미지 처리
                    FileInfo fileInfo = fileService.uploadFile(file);
                    BoardFile boardFile = BoardFile.createImage(
                            fileInfo.getOriginalName(),
                            fileInfo.getStoredName(),
                            fileInfo.getFilePath(),
                            fileInfo.getFileSize(),
                            mimeType,
                            i
                    );
                    board.addFile(boardFile);
                } else if (mimeType.startsWith("video/")) {
                    // 동영상 처리
                    VideoInfo videoInfo = videoService.uploadVideo(file);
                    BoardFile boardFile = BoardFile.createVideo(
                            videoInfo.getOriginalName(),
                            videoInfo.getStoredName(),
                            videoInfo.getFilePath(),
                            videoInfo.getFileSize(),
                            mimeType,
                            i,
                            videoInfo.getDuration(),
                            videoInfo.getThumbnailPath()
                    );
                    board.addFile(boardFile);
                }
            }
        }

        return boardRepository.save(board);
    }
}