package com.example.hello_friends.board.application.service;

import com.example.hello_friends.board.application.request.VideoInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

            // 썸네일 생성
            String thumbnailPath = generateThumbnail(fullPath, storedFilename);

            // 동영상 길이 추출
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
        LocalDateTime now = LocalDateTime.now();
        return now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    }

    private String generateThumbnail(String path, String filename) {
        try {
            String thumbnailName = "thumb_" + filename + ".jpg";
            String thumbnailPath = path + File.separator + thumbnailName;

            // FFmpeg 명령어 구성
            ProcessBuilder pb = new ProcessBuilder(
                    "ffmpeg",
                    "-i", path + File.separator + filename,
                    "-ss", "00:00:01",  // 1초 지점
                    "-vframes", "1",     // 1개 프레임
                    "-s", "320x240",     // 썸네일 크기
                    thumbnailPath
            );

            Process process = pb.start();
            process.waitFor();

            if (process.exitValue() != 0) {
                throw new RuntimeException("썸네일 생성 실패");
            }

            return thumbnailPath;
        } catch (Exception e) {
            log.error("썸네일 생성 실패: ", e);
            throw new RuntimeException("썸네일 생성 중 오류가 발생했습니다.", e);
        }
    }

    private Integer extractDuration(String videoPath) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "ffprobe",
                    "-v", "error",
                    "-show_entries", "format=duration",
                    "-of", "default=noprint_wrappers=1:nokey=1",
                    videoPath
            );

            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String duration = reader.readLine();
            process.waitFor();

            if (process.exitValue() != 0) {
                throw new RuntimeException("동영상 길이 추출 실패");
            }

            // 초 단위로 반올림하여 반환
            return (int) Math.round(Double.parseDouble(duration));
        } catch (Exception e) {
            log.error("동영상 길이 추출 실패: ", e);
            throw new RuntimeException("동영상 길이 추출 중 오류가 발생했습니다.", e);
        }
    }
}