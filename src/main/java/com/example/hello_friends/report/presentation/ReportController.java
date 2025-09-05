package com.example.hello_friends.report.presentation;

import com.example.hello_friends.common.response.Resp;
import com.example.hello_friends.report.application.request.ReportRequest;
import com.example.hello_friends.report.application.response.ReportResponse;
import com.example.hello_friends.report.application.service.ReportService;
import com.example.hello_friends.security.annotation.Auth;
import com.example.hello_friends.security.filter.JwtPrincipalDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "신고")
public class ReportController {
    private final ReportService reportService;

    @Operation(summary = "컨텐츠 신고", description = "게시글 또는 댓글을 신고합니다.")
    @PostMapping("/api/user/report")
    public Resp<String> createReport(@RequestBody ReportRequest reportRequest, @Auth JwtPrincipalDto jwtPrincipalDto) {
        reportService.createReport(
                jwtPrincipalDto.getId(),
                reportRequest.getReportType(),
                reportRequest.getContentId(),
                reportRequest.getReason()
        );

        return Resp.ok("신고가 성공적으로 접수되었습니다.");
    }

    @Operation(summary = "신고 내용 조회", description = "신고 내용을 조회합니다.")
    @GetMapping("/api/admin/reports/{id}")
    public ReportResponse getReport(@PathVariable Long id){
        return reportService.getReport(id);
    }

    @Operation(summary = "신고 목록 조회", description = "신고 내용 목록을 조회합니다.")
    @GetMapping("/api/admin/reports")
    public Resp<List<ReportResponse>> getReportList(){
        return Resp.ok(reportService.getReportList());
    }
}
