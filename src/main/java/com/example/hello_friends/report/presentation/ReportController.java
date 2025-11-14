package com.example.hello_friends.report.presentation;

import com.example.hello_friends.common.response.Resp;
import com.example.hello_friends.report.application.request.ReportProcessRequest;
import com.example.hello_friends.report.application.request.ReportRequest;
import com.example.hello_friends.report.application.response.ReportResponse;
import com.example.hello_friends.report.application.service.ReportService;
import com.example.hello_friends.security.annotation.Auth;
import com.example.hello_friends.security.filter.JwtPrincipalDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "신고")
public class ReportController {
    private final ReportService reportService;

    @Operation(summary = "컨텐츠 신고", description = "게시글 또는 댓글을 신고합니다.")
    @PostMapping("/api/user/report")
    public ResponseEntity<Resp<String>> createReport(@RequestBody ReportRequest reportRequest, @Parameter(hidden = true) @Auth JwtPrincipalDto jwtPrincipalDto) {
        reportService.createReport(
                jwtPrincipalDto.getId(),
                reportRequest.getReportType(),
                reportRequest.getContentId(),
                reportRequest.getReason()
        );

        return ResponseEntity.ok(Resp.ok("신고가 성공적으로 접수되었습니다."));
    }

    @Operation(summary = "신고 내용 조회", description = "신고 내용을 조회합니다.")
    @GetMapping("/api/admin/reports/{id}")
    public ResponseEntity<Resp<ReportResponse>> getReport(@PathVariable Long id){
        return ResponseEntity.ok(Resp.ok(reportService.getReport(id)));
    }

    @Operation(summary = "신고 목록 조회", description = "신고 내용 목록을 조회합니다.")
    @GetMapping("/api/admin/reports")
    public ResponseEntity<Resp<List<ReportResponse>>> getReportList(){
        return ResponseEntity.ok(Resp.ok(reportService.getReportList()));
    }

    @Operation(summary = "관리자 신고 처리", description = "관리자가 신고를 승인 또는 반려 처리합니다.")
    @PatchMapping("/api/admin/reports/{reportId}")
    public ResponseEntity<Resp<ReportResponse>> processReport(
            @PathVariable Long reportId,
            @RequestBody ReportProcessRequest reportProcessRequest,
            @Parameter(hidden = true) @Auth JwtPrincipalDto jwtPrincipalDto // ADMIN 권한 검증
    ) {
        ReportResponse response = reportService.processReport(
                reportId,
                reportProcessRequest.isAccepted(),
                reportProcessRequest.getAdminMemo()
        );

        return ResponseEntity.ok(Resp.ok(response));
    }
}
