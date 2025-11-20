package com.example.hello_friends.board.application.service;

import com.example.hello_friends.board.domain.BoardFile;
import com.example.hello_friends.board.domain.BoardFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
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

    private final BoardFileRepository boardFileRepository;

    // 비동기(@Async)가 같은 클래스 내에서 작동하도록 자기 자신을 주입
    // 순환 참조 방지를 위해 @Lazy를 꼭 붙여야 해요.
    @Autowired
    @Lazy
    private VideoService self;

    // 동영상 저장 후 비동기 처리 요청
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

            // 사용자가 기다리지 않고 바로 응답받기 가능
            self.processVideoFile(boardFile.getId(), fullDirPath.toString(), storedFilename);

            return boardFile;

        } catch (IOException e) {
            log.error("비디오 파일 저장 실패: {}", e.getMessage());
            throw new RuntimeException("비디오 업로드 중 초기 오류가 발생했습니다.", e);
        }
    }

    // 썸네일 및 길이 추출
    @Async
    @Transactional
    public void processVideoFile(Long fileId, String dirPath, String storedFilename) {
        log.info("비동기 비디오 처리 시작: fileId={}", fileId);
        String relativeDatePath = createDatePath();

        try {
            Path videoFullPath = Paths.get(dirPath, storedFilename);

            // 썸네일 생성
            String thumbnailName = "thumb_" + storedFilename.substring(0, storedFilename.lastIndexOf(".")) + ".jpg";
            String thumbnailRelativePath = Paths.get(relativeDatePath, thumbnailName).toString();

            // 썸네일 만들기 (오래 걸림)
            generateThumbnail(dirPath, storedFilename, thumbnailName);

            // 길이 추출하기
            Integer duration = extractDuration(videoFullPath.toString());

            // DB 업데이트
            BoardFile boardFile = boardFileRepository.findById(fileId)
                    .orElseThrow(() -> new RuntimeException("비디오 처리 중 파일을 찾을 수 없음: " + fileId));

            boardFile.processingComplete(duration, thumbnailRelativePath);
            log.info("비동기 비디오 처리 성공: fileId={}", fileId);

        } catch (Exception e) {
            log.error("비동기 비디오 처리 실패: fileId={}, error={}", fileId, e.getMessage());
            // 실패 시 DB에 실패 사유 기록
            boardFileRepository.findById(fileId).ifPresent(bf -> {
                bf.processingFailed(e.getMessage());
            });
        }
    }

    // 썸네일 생성 - 무한 대기 해결 (inheritIO 사용)
    private void generateThumbnail(String dirPath, String inputFilename, String outputThumbnailName) throws IOException, InterruptedException {
        Path inputPath = Paths.get(dirPath, inputFilename);
        Path outputPath = Paths.get(dirPath, outputThumbnailName);

        // FFmpeg 명령어: 1초 지점(-ss 00:00:01)에서 사진 1장(-vframes 1)을 찍어라
        ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg", "-i", inputPath.toString(),
                "-ss", "00:00:01", "-vframes", "1", "-s", "320x240",
                outputPath.toString()
        );

        // FFmpeg의 로그를 자바 콘솔로 바로 연결해서 버퍼가 꽉차는것 방지
        pb.inheritIO();

        Process process = pb.start();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("썸네일 생성 실패 (FFmpeg 에러)");
        }
    }

    // 길이 추출 - 결과값 읽기
    private Integer extractDuration(String videoPath) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(
                "ffprobe",
                "-v", "error",
                "-show_entries", "format=duration",
                "-of", "default=noprint_wrappers=1:nokey=1",
                videoPath
        );

        Process process = pb.start();

        // 결과를 읽어와야 하므로 BufferedReader 사용
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String durationStr = reader.readLine();

            process.waitFor();

            if (process.exitValue() != 0) {
                throw new RuntimeException("동영상 길이 추출 실패 (FFprobe 에러)");
            }

            if (durationStr == null || durationStr.trim().isEmpty()) {
                return 0;
            }

            return (int) Math.round(Double.parseDouble(durationStr));
        }
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