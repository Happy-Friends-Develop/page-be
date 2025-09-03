package com.example.hello_friends.board.application.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VideoInfo {
    private String originalName;    // 원본 파일명
    private String storedName;      // 저장된 파일명
    private String filePath;        // 파일 경로
    private Long fileSize;          // 파일 크기
    private Integer duration;        // 동영상 길이(초)
    private String thumbnailPath;   // 썸네일 경로
}
