package com.example.hello_friends.report.application.response;

import com.example.hello_friends.report.application.reportEnum.ReportStatus;
import com.example.hello_friends.report.application.reportEnum.ReportType;
import com.example.hello_friends.report.domain.Report;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReportResponse {
    private Long id;
    private String reporterNickname; // 신고한 사람
    private String reportedUserNickname; // 신고당한 사람
    private ReportType reportType;
    private Long contentId;
    private String reason;
    private ReportStatus status;
    private LocalDateTime createdAt;

    public static ReportResponse from(Report report) {
        ReportResponse dto = new ReportResponse();
        dto.setId(report.getId());
        dto.setReporterNickname(report.getReporter().getNickname());
        dto.setReportedUserNickname(report.getReportedUser().getNickname());
        dto.setReportType(report.getReportType());
        dto.setContentId(report.getContentId());
        dto.setReason(report.getReason());
        dto.setStatus(report.getStatus());
        dto.setCreatedAt(report.getCreatedAt());
        return dto;
    }
}
