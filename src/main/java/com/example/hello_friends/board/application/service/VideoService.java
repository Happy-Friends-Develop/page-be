package com.example.hello_friends.board.application.service;

import com.example.hello_friends.board.application.request.VideoInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;    // 여기가 수정된 부분!
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoService {
    @Value("${file.upload.path}")
    private String uploadPath;

    @Value("${file.upload.video}")
    private String videoPath;

    public VideoInfo uploadVideo(MultipartFile file) {
        try {
            // 원본 파일명
            String originalFilename = file.getOriginalFilename();

            // 저장할 파일명 생성 (UUID 사용)
            String storedFilename = UUID.randomUUID().toString() +
                    getFileExtension(originalFilename);

            // 저장 경로 생성
            String datePath = createDatePath();
            String fullPath = uploadPath + File.separator +
                    videoPath + File.separator + datePath;

            // 디렉토리 생성
            File directory = new File(fullPath);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // 파일 저장
            File destFile = new File(fullPath + File.separator + storedFilename);
            file.transferTo(destFile);

            // 썸네일 생성 (FFmpeg 등을 사용하여 구현)
            String thumbnailPath = generateThumbnail(fullPath, storedFilename);

            // 동영상 길이 추출 (FFmpeg 등을 사용하여 구현)
            Integer duration = extractDuration(fullPath + File.separator + storedFilename);

            return VideoInfo.builder()
                    .originalName(originalFilename)
                    .storedName(storedFilename)
                    .filePath(datePath + File.separator + storedFilename)
                    .fileSize(file.getSize())
                    .duration(duration)
                    .thumbnailPath(thumbnailPath)
                    .build();

        } catch (IOException e) {
            log.error("비디오 업로드 실패: ", e);
            throw new RuntimeException("비디오 업로드 중 오류가 발생했습니다.", e);
        }
    }

    private String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf("."));
    }

    private String createDatePath() {
        // 년/월/일 형식의 경로 생성
        return "2025/03/03"; // 실제로는 현재 날짜 기반으로 생성
    }

    private String generateThumbnail(String path, String filename) {
        // FFmpeg를 사용하여 썸네일 생성
        // 실제 구현 필요
        return path + "/thumb_" + filename + ".jpg";
    }

    private Integer extractDuration(String videoPath) {
        // FFmpeg를 사용하여 동영상 길이 추출
        // 실제 구현 필요
        return 180; // 예시 값
    }
}