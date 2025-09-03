package com.example.hello_friends.board.application.service;

import com.example.hello_friends.board.application.request.FileInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileService {
    @Value("${file.upload.path}")
    private String uploadPath;

    public FileInfo uploadFile(MultipartFile file) {
        try {
            // 원본 파일명
            String originalFilename = file.getOriginalFilename();

            // 저장할 파일명 생성 (UUID 사용)
            String storedFilename = UUID.randomUUID().toString() +
                    getFileExtension(originalFilename);

            // 저장 경로 생성
            String datePath = createDatePath();
            String fullPath = uploadPath + File.separator + datePath;

            // 디렉토리 생성
            File directory = new File(fullPath);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // 파일 저장
            File destFile = new File(fullPath + File.separator + storedFilename);
            file.transferTo(destFile);

            return FileInfo.builder()
                    .originalName(originalFilename)
                    .storedName(storedFilename)
                    .filePath(datePath + File.separator + storedFilename)
                    .fileSize(file.getSize())
                    .build();

        } catch (IOException e) {
            log.error("파일 업로드 실패: ", e);
            throw new RuntimeException("파일 업로드 중 오류가 발생했습니다.", e);
        }
    }

    private String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf("."));
    }

    private String createDatePath() {
        LocalDateTime now = LocalDateTime.now();
        return now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    }
}