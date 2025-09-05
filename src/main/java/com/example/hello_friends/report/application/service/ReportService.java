package com.example.hello_friends.report.application.service;

import com.example.hello_friends.board.domain.Board;
import com.example.hello_friends.board.domain.BoardRepository;
import com.example.hello_friends.comment.domain.Comment;
import com.example.hello_friends.comment.domain.CommentRepository;
import com.example.hello_friends.report.application.reportEnum.ReportType;
import com.example.hello_friends.report.application.response.ReportResponse;
import com.example.hello_friends.report.domain.Report;
import com.example.hello_friends.report.domain.ReportRepository;
import com.example.hello_friends.user.domain.User;
import com.example.hello_friends.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
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

    @Transactional
    public void createReport(Long reporterId, ReportType reportType, Long contentId, String reason) {
        // 신고자 정보
        User reporter = userRepository.findById(reporterId)
                .orElseThrow(() -> new IllegalArgumentException("신고자를 찾을 수 없습니다."));

        // 피신고자 정보
        User reportedUser = findContentAuthor(reportType, contentId);

        // 중복 신고인지 확인
        if (reportRepository.existsByReporterAndReportTypeAndContentId(reporter, reportType, contentId)) {
            throw new IllegalStateException("이미 신고한 컨텐츠입니다.");
        }

        // 신고 완료
        Report report = new Report(reporter, reportedUser, reportType, contentId, reason);
        reportRepository.save(report);
    }

    // 신고된 컨텐츠 찾는 메서드
    private User findContentAuthor(ReportType reportType, Long contentId) {
        if (reportType == ReportType.BOARD) {
            Board board = boardRepository.findById(contentId)
                    .orElseThrow(() -> new IllegalArgumentException("신고하려는 게시글을 찾을 수 없습니다."));
            return board.getUser();
        } else if (reportType == ReportType.COMMENT) {
            Comment comment = commentRepository.findById(contentId)
                    .orElseThrow(() -> new IllegalArgumentException("신고하려는 댓글을 찾을 수 없습니다."));
            return comment.getUser();
        }
        throw new IllegalArgumentException("알 수 없는 컨텐츠 타입입니다.");
    }

    // 신고한 내용 조회
    @Transactional(readOnly = true)
    public ReportResponse getReport(Long id){
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 신고가 없습니다. id=" + id));

        return ReportResponse.from(report);
    }

    // 신고 목록 조회
    @Transactional(readOnly = true)
    public List<ReportResponse> getReportList(){
        List<Report> reportList = reportRepository.findAll();

        return reportList.stream()
                .map(ReportResponse::from)
                .toList();
    }
}
