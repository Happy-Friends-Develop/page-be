package com.example.hello_friends.board.application.response;

import com.example.hello_friends.board.domain.BoardFile;
import com.example.hello_friends.board.domain.FileType;
import lombok.Builder;
import lombok.Data;

@Data
public class BoardFileResponse {
    private Long id;
    private String originalName; // 원래 파일 이름
    private String filePath;     // 파일 경로 (이미지 주소)
    private FileType fileType;   // IMAGE 또는 VIDEO
    private String thumbnailPath; // (동영상일 경우) 썸네일 경로

    @Builder
    public BoardFileResponse(Long id, String originalName, String filePath, FileType fileType, String thumbnailPath) {
        this.id = id;
        this.originalName = originalName;
        this.filePath = filePath;
        this.fileType = fileType;
        this.thumbnailPath = thumbnailPath;
    }

    public static BoardFileResponse from(BoardFile boardFile) {
        return BoardFileResponse.builder()
                .id(boardFile.getId())
                .originalName(boardFile.getOriginalName())
                .filePath(boardFile.getFilePath())
                .fileType(boardFile.getFileType())
                .thumbnailPath(boardFile.getThumbnailPath())
                .build();
    }
}