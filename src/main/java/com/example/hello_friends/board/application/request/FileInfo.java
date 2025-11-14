package com.example.hello_friends.board.application.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FileInfo {
    private String originalName;    // 원본 파일명
    private String storedName;      // 저장된 파일명
    private String filePath;        // 파일 경로
    private long fileSize;          // 파일 크기
}

