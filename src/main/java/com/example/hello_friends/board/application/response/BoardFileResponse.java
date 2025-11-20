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
        // 윈도우 경로(\)를 웹 표준 경로(/)로 수정
        String storedPath = boardFile.getFilePath().replace("\\", "/");

        // 파일 종류에 따라 문패(Prefix) 다르게 달기
        String urlPrefix = "";

        if (boardFile.getFileType() == FileType.IMAGE) {
            urlPrefix = "/images/";
        } else if (boardFile.getFileType() == FileType.VIDEO) {
            urlPrefix = "/videos/";
        }

        // 최종 주소
        String publicUrl = urlPrefix + storedPath;

        // 썸네일 주소 처리 - 동영상만
        String thumbnailPublicUrl = null;
        if (boardFile.getThumbnailPath() != null) {
            String thumbPath = boardFile.getThumbnailPath().replace("\\", "/");
            thumbnailPublicUrl = "/images/" + thumbPath;
        }

        return BoardFileResponse.builder()
                .id(boardFile.getId())
                .originalName(boardFile.getOriginalName())
                .filePath(publicUrl)
                .fileType(boardFile.getFileType())
                .thumbnailPath(thumbnailPublicUrl)
                .build();
    }
}