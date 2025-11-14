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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {
    //@Value("${file.upload.path}")
    private String uploadPath;

    //@Value("${file.upload.image}")
    private String imagePath;

    private final BoardFileRepository boardFileRepository; // DB 작업을 위해 Repository 주입

    // 이미지 업로드 요청을 받고 파일을 저장한 뒤, 즉시 사용자에게 응답.
    @Transactional
    public BoardFile uploadImageAndRequestProcessing(MultipartFile file, String mimeType, Integer sortOrder) {
        try {
            String originalFilename = file.getOriginalFilename();
            String storedFilename = UUID.randomUUID() + getFileExtension(originalFilename);
            String datePath = createDatePath();

            Path fullDirPath = Paths.get(uploadPath, imagePath, datePath);
            Files.createDirectories(fullDirPath);

            Path destPath = fullDirPath.resolve(storedFilename);
            file.transferTo(destPath);

            BoardFile boardFile = BoardFile.createImageForProcessing(
                    originalFilename,
                    storedFilename,
                    Paths.get(datePath, storedFilename).toString(),
                    file.getSize(),
                    mimeType,
                    sortOrder
            );
            boardFileRepository.save(boardFile);

            processImageFile(boardFile.getId());

            return boardFile;

        } catch (IOException e) {
            log.error("이미지 파일 저장 실패: {}", e.getMessage());
            throw new RuntimeException("이미지 업로드 중 초기 오류가 발생했습니다.", e);
        }
    }

    // 시간이 걸릴 수 있는 이미지 후처리 작업을 백그라운드에서 수행.
    @Async
    @Transactional
    public void processImageFile(Long fileId) {
        log.info("비동기 이미지 처리 시작: fileId={}", fileId);
        try {
            Thread.sleep(1000);

            BoardFile boardFile = boardFileRepository.findById(fileId)
                    .orElseThrow(() -> new RuntimeException("이미지 처리 중 파일을 찾을 수 없음: " + fileId));

            boardFile.processingComplete(null, null); // 이미지에는 별도 정보가 없으므로 null 전달
            log.info("비동기 이미지 처리 성공: fileId={}", fileId);

        } catch (Exception e) {
            log.error("비동기 이미지 처리 실패: fileId={}, error={}", fileId, e.getMessage());
            boardFileRepository.findById(fileId).ifPresent(bf -> {
                bf.processingFailed(e.getMessage());
            });
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