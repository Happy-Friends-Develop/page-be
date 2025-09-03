package com.example.hello_friends.board.domain;

import com.example.hello_friends.common.entity.EntityState;
import com.example.hello_friends.common.entity.LogEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "board_file")
public class BoardFile extends LogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @Enumerated(EnumType.STRING)
    @Column(name = "file_type", nullable = false)
    private FileType fileType;    // 이미지/동영상 구분

    @Column(name = "original_name")
    private String originalName;  // 원본 파일명

    @Column(name = "stored_name")
    private String storedName;    // 저장된 파일명

    @Column(name = "file_path")
    private String filePath;      // 파일 경로

    @Column(name = "file_size")
    private Long fileSize;        // 파일 크기

    @Column(name = "mime_type")
    private String mimeType;      // MIME 타입

    @Column(name = "sort_order")
    private Integer sortOrder;    // 정렬 순서

    // 동영상 관련 추가 필드
    @Column(name = "duration")
    private Integer duration;     // 동영상 길이(초)

    @Column(name = "thumbnail_path")
    private String thumbnailPath; // 동영상 썸네일 경로

    @Column(name = "file_state", nullable = false)
    @Enumerated(EnumType.STRING)
    private EntityState state;

    @Enumerated(EnumType.STRING)
    @Column(name = "process_status")
    private FileProcessStatus processStatus; // 처리 상태 필드 추가

    @Column(name = "process_fail_reason", length = 1000)
    private String processFailReason; // 처리 실패 시 원인을 기록할 필드

    // Board 설정 메서드
    protected void setBoard(Board board) {
        this.board = board;
    }

    // 동영상 파일 초기 생성
    public static BoardFile createVideoForProcessing(String originalName, String storedName,
                                                     String filePath, Long fileSize, String mimeType, Integer sortOrder) {
        BoardFile file = new BoardFile();
        file.fileType = FileType.VIDEO;
        file.processStatus = FileProcessStatus.PROCESSING;
        file.state = EntityState.ACTIVE;
        file.originalName = originalName;
        file.storedName = storedName;
        file.filePath = filePath;
        file.fileSize = fileSize;
        file.mimeType = mimeType;
        file.sortOrder = sortOrder;
        return file;
    }

    // 비동기 처리 완료 후 정보를 업데이트하는 메소드
    public void processingComplete(Integer duration, String thumbnailPath) {
        this.duration = duration;
        this.thumbnailPath = thumbnailPath;
        this.processStatus = FileProcessStatus.COMPLETE;
    }

    // 비동기 처리 실패 시 정보를 업데이트하는 메소드
    public void processingFailed(String reason) {
        this.processStatus = FileProcessStatus.FAILED;
        this.processFailReason = reason;
    }

    // 이미지 파일 초기 생성
    public static BoardFile createImageForProcessing(String originalName, String storedName,
                                                     String filePath, Long fileSize,
                                                     String mimeType, Integer sortOrder) {
        BoardFile file = new BoardFile();
        file.fileType = FileType.IMAGE;
        file.processStatus = FileProcessStatus.PROCESSING;
        file.state = EntityState.ACTIVE;
        file.originalName = originalName;
        file.storedName = storedName;
        file.filePath = filePath;
        file.fileSize = fileSize;
        file.mimeType = mimeType;
        file.sortOrder = sortOrder;
        return file;
    }
}