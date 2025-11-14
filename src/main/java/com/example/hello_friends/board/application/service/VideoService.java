package com.example.hello_friends.board.application.service;

import com.example.hello_friends.board.domain.BoardFile;
import com.example.hello_friends.board.domain.BoardFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoService {
    @Value("${file.upload.path}")
    private String uploadPath;

    @Value("${file.upload.video}")
    private String videoPath;

    private final BoardFileRepository boardFileRepository; // DB 작업을 위해 Repository 주입

    // 동영상 업로드 요청을 받고 파일을 저장한 뒤, 즉시 사용자에게 응답.
    @Transactional
    public BoardFile uploadVideoAndRequestProcessing(MultipartFile file, String mimeType, Integer sortOrder) {
        try {
            String originalFilename = file.getOriginalFilename();
            String storedFilename = UUID.randomUUID() + getFileExtension(originalFilename);
            String datePath = createDatePath();
            Path fullDirPath = Paths.get(uploadPath, videoPath, datePath);
            Files.createDirectories(fullDirPath);
            Path destPath = fullDirPath.resolve(storedFilename);
            file.transferTo(destPath);

            BoardFile boardFile = BoardFile.createVideoForProcessing(
                    originalFilename,
                    storedFilename,
                    Paths.get(datePath, storedFilename).toString(),
                    file.getSize(),
                    mimeType,
                    sortOrder
            );
            boardFileRepository.save(boardFile);

            processVideoFile(boardFile.getId(), fullDirPath.toString(), storedFilename);

            return boardFile;

        } catch (IOException e) {
            log.error("비디오 파일 저장 실패: {}", e.getMessage());
            throw new RuntimeException("비디오 업로드 중 초기 오류가 발생했습니다.", e);
        }
    }

    // 2단계 (비동기): 시간이 오래 걸리는 썸네일 생성 및 길이 추출 작업을 배경에서 처리.
    @Async
    @Transactional
    public void processVideoFile(Long fileId, String dirPath, String storedFilename) {
        log.info("비동기 비디오 처리 시작: fileId={}", fileId);
        String relativeDatePath = createDatePath(); // 상대 경로를 일관되게 사용하기 위해
        try {
            Path videoFullPath = Paths.get(dirPath, storedFilename);

            // 썸네일 생성
            String thumbnailName = "thumb_" + storedFilename.substring(0, storedFilename.lastIndexOf(".")) + ".jpg";
            String thumbnailRelativePath = Paths.get(relativeDatePath, thumbnailName).toString();
            generateThumbnail(dirPath, storedFilename, thumbnailName);

            // 동영상 길이 추출
            Integer duration = extractDuration(videoFullPath.toString());

            // DB에서 다시 파일 정보를 가져와서 업데이트
            BoardFile boardFile = boardFileRepository.findById(fileId)
                    .orElseThrow(() -> new RuntimeException("비디오 처리 중 파일을 찾을 수 없음: " + fileId));

            boardFile.processingComplete(duration, thumbnailRelativePath);
            log.info("비동기 비디오 처리 성공: fileId={}", fileId);

        } catch (Exception e) {
            log.error("비동기 비디오 처리 실패: fileId={}, error={}", fileId, e.getMessage());

            boardFileRepository.findById(fileId).ifPresent(bf -> {
                bf.processingFailed(e.getMessage());
            });
        }
    }

    private void generateThumbnail(String dirPath, String inputFilename, String outputThumbnailName) throws IOException, InterruptedException {
        Path inputPath = Paths.get(dirPath, inputFilename);
        Path outputPath = Paths.get(dirPath, outputThumbnailName);

        ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg", "-i", inputPath.toString(),
                "-ss", "00:00:01", "-vframes", "1", "-s", "320x240",
                outputPath.toString()
        );
        pb.redirectErrorStream(true);

        Process process = pb.start();
        process.waitFor();

        if (process.exitValue() != 0) {
            String errorMessage = new BufferedReader(new InputStreamReader(process.getInputStream()))
                    .lines().collect(Collectors.joining("\n"));
            throw new RuntimeException("썸네일 생성 실패. FFmpeg 오류: " + errorMessage);
        }
    }

    private Integer extractDuration(String videoPath) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(
                "ffprobe",
                "-v", "error",
                "-show_entries", "format=duration",
                "-of", "default=noprint_wrappers=1:nokey=1",
                videoPath
        );

        Process process = pb.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String durationStr = reader.readLine();
        process.waitFor();

        if (process.exitValue() != 0) {
            String errorMessage = new BufferedReader(new InputStreamReader(process.getErrorStream()))
                    .lines().collect(Collectors.joining("\n"));
            throw new RuntimeException("동영상 길이 추출 실패. FFprobe 오류: " + errorMessage);
        }

        if (durationStr == null || durationStr.trim().isEmpty()) {
            throw new RuntimeException("동영상 길이를 추출할 수 없음. FFprobe 출력값이 비어있음.");
        }

        return (int) Math.round(Double.parseDouble(durationStr));
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    private String createDatePath() {
        LocalDateTime now = LocalDateTime.now();
        return now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    }
}