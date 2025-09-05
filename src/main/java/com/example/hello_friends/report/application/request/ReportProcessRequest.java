package com.example.hello_friends.report.application.request;

import lombok.Data;

@Data
public class ReportProcessRequest {
    // 신고를 승인할지 여부 (true: 승인, false: 반려)
    private boolean accepted;

    // 처리 시 남길 관리자 메모
    private String adminMemo;
}
