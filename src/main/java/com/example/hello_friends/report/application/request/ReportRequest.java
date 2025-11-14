package com.example.hello_friends.report.application.request;

import com.example.hello_friends.report.application.reportEnum.ReportType;
import lombok.Data;

@Data
public class ReportRequest {
    // 무엇을 신고했는지 (BOARD 또는 COMMENT)
    private ReportType reportType;

    // 신고할 게시글 또는 댓글의 ID
    private Long contentId;

    // 신고 사유
    private String reason;
}
