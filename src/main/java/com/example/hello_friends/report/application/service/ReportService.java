package com.example.hello_friends.report.application.service;

import com.example.hello_friends.board.domain.Board;
import com.example.hello_friends.board.domain.BoardRepository;
import com.example.hello_friends.comment.domain.Comment;
import com.example.hello_friends.comment.domain.CommentRepository;
import com.example.hello_friends.common.exception.BoardNotFoundException;
import com.example.hello_friends.common.exception.CommentNotFoundException;
import com.example.hello_friends.common.exception.ReportNotFoundException;
import com.example.hello_friends.common.exception.UserNotFoundException;
import com.example.hello_friends.common.response.MotherException;
import com.example.hello_friends.notification.application.service.NotificationService;
import com.example.hello_friends.report.application.reportEnum.ReportStatus;
import com.example.hello_friends.report.application.reportEnum.ReportType;
import com.example.hello_friends.report.application.response.ReportResponse;
import com.example.hello_friends.report.domain.Report;
import com.example.hello_friends.report.domain.ReportRepository;
import com.example.hello_friends.user.application.service.BlackUserService;
import com.example.hello_friends.user.domain.User;
import com.example.hello_friends.user.domain.UserRepository;
import com.example.hello_friends.user.domain.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final BlackUserService blackUserService;
    private final NotificationService notificationService;

    @Transactional
    public void createReport(Long reporterId, ReportType reportType, Long contentId, String reason) {
        // 신고자 정보
        User reporter = userRepository.findById(reporterId)
                .orElseThrow(() -> new UserNotFoundException("신고자를 찾을 수 없습니다. ID : " + reporterId));
        User reportedUser = findContentAuthor(reportType, contentId);

        if (reportRepository.existsByReporterAndReportTypeAndContentId(reporter, reportType, contentId)) {
            throw new MotherException("이미 신고한 컨텐츠입니다.", HttpStatus.BAD_REQUEST);
        }

        Report report = new Report(reporter, reportedUser, reportType, contentId, reason);
        reportRepository.save(report);

        // 관리자 계정을 조회
        List<User> admins = userRepository.findAllByUserRole(UserRole.ADMIN);

        // 각 관리자에게 알림 전송
        for (User admin : admins) {
            String notificationContent = "새로운 신고가 접수되었습니다. 확인이 필요합니다.";
            // 알림 클릭 시 이동할 URL (관리자 신고 상세 페이지)
            String notificationUrl = "/admin/reports/" + report.getId();
            notificationService.send(admin, notificationContent, notificationUrl);
        }
    }

    // 신고된 컨텐츠 찾는 메서드
    private User findContentAuthor(ReportType reportType, Long contentId) {
        if (reportType == ReportType.BOARD) {
            Board board = boardRepository.findById(contentId)
                    .orElseThrow(() -> new BoardNotFoundException("신고하려는 게시글을 찾을 수 없습니다. ID : " + contentId));
            return board.getUser();
        } else if (reportType == ReportType.COMMENT) {
            Comment comment = commentRepository.findById(contentId)
                    .orElseThrow(() -> new CommentNotFoundException("신고하려는 댓글을 찾을 수 없습니다. ID : " + contentId));
            return comment.getUser();
        }
        throw new MotherException("알 수 없는 컨텐츠 타입입니다.", HttpStatus.BAD_REQUEST);
    }

    // 신고한 내용 조회
    @Transactional(readOnly = true)
    public ReportResponse getReport(Long id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new ReportNotFoundException("해당 신고가 없습니다. ID : " + id));

        return ReportResponse.from(report);
    }

    // 신고 목록 조회
    @Transactional(readOnly = true)
    public List<ReportResponse> getReportList() {
        List<Report> reportList = reportRepository.findAll();

        return reportList.stream()
                .map(ReportResponse::from)
                .toList();
    }

    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ReportResponse processReport(Long reportId, boolean isAccepted, String adminMemo) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ReportNotFoundException("처리할 신고를 찾을 수 없습니다. ID : " + reportId));

        // 중복 처리 방지
        if (report.getStatus() != ReportStatus.PENDING) {
            throw new IllegalStateException("이미 처리된 신고입니다.");
        }

        if (isAccepted) {
            // 신고를 승인
            report.process(ReportStatus.ACCEPTED, adminMemo);

            // 신고당한 사용자에게 경고를 1회 추가
            Long reportedUserId = report.getReportedUser().getId();
            String warningReason = "신고 접수됨: [" + report.getReportType() + "] " + report.getReason();
            blackUserService.addWarningAndBlacklistIfNeeded(reportedUserId, warningReason, adminMemo);

        } else {
            // 신고를 반려
            report.process(ReportStatus.REJECTED, adminMemo);
        }

        return ReportResponse.from(report);
    }
}
