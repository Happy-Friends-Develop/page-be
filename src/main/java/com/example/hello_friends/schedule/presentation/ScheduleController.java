package com.example.hello_friends.schedule.presentation;

import com.example.hello_friends.common.response.Resp;
import com.example.hello_friends.schedule.application.request.ScheduleRequest;
import com.example.hello_friends.schedule.application.request.ScheduleUpdateRequest;
import com.example.hello_friends.schedule.application.response.ScheduleResponse;
import com.example.hello_friends.schedule.application.serivce.ScheduleService;
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
@Tag(name = "스케줄 관리")
public class ScheduleController {
    private final ScheduleService scheduleService;

    @Operation(summary = "게시글의 스케줄 목록 조회", description = "특정 게시글에 포함된 모든 예약 옵션(스케줄)을 조회합니다.")
    @GetMapping("/api/user/boards/{boardId}/schedules")
    public ResponseEntity<Resp<List<ScheduleResponse>>> getSchedulesForBoard(
            @Parameter(description = "게시글의 ID") @PathVariable Long boardId
    ) {
        List<ScheduleResponse> schedules = scheduleService.getSchedulesForBoard(boardId);
        return ResponseEntity.ok(Resp.ok(schedules));
    }

    @Operation(summary = "게시글에 스케줄 추가", description = "특정 게시글에 하나 이상의 예약 옵션(스케줄)을 추가합니다.")
    @PostMapping("/api/user/boards/{boardId}/schedules")
    public ResponseEntity<Resp<String>> addSchedulesToBoard(
            @Parameter(description = "스케줄을 추가할 게시글의 ID") @PathVariable Long boardId,
            @RequestBody List<ScheduleRequest> requests,
            @Parameter(hidden = true) @Auth JwtPrincipalDto jwtPrincipalDto
    ) {
        scheduleService.addSchedulesToBoard(boardId, requests, jwtPrincipalDto.getId());
        return ResponseEntity.ok(Resp.ok("스케줄이 성공적으로 추가되었습니다."));
    }


    @Operation(summary = "스케줄 수정", description = "특정 스케줄의 날짜, 정원, 가격 등을 수정합니다.")
    @PutMapping("/api/user/boards/{boardId}/schedules/{scheduleId}")
    public ResponseEntity<Resp<String>> updateSchedule(
            @Parameter(description = "게시글의 ID") @PathVariable Long boardId,
            @Parameter(description = "수정할 스케줄의 ID") @PathVariable Long scheduleId,
            @RequestBody ScheduleUpdateRequest request,
            @Parameter(hidden = true) @Auth JwtPrincipalDto jwtPrincipalDto
    ) {
        scheduleService.updateSchedule(boardId, scheduleId, jwtPrincipalDto.getId(), request);
        return ResponseEntity.ok(Resp.ok("스케줄이 성공적으로 수정되었습니다."));
    }


    @Operation(summary = "스케줄 삭제", description = "특정 스케줄을 삭제합니다. 단, 예약자가 있는 스케줄은 삭제할 수 없습니다.")
    @DeleteMapping("/api/user/boards/{boardId}/schedules/{scheduleId}")
    public ResponseEntity<Resp<String>> deleteSchedule(
            @Parameter(description = "게시글의 ID") @PathVariable Long boardId,
            @Parameter(description = "삭제할 스케줄의 ID") @PathVariable Long scheduleId,
            @Parameter(hidden = true) @Auth JwtPrincipalDto jwtPrincipalDto
    ) {
        scheduleService.deleteSchedule(boardId, scheduleId, jwtPrincipalDto.getId());
        return ResponseEntity.ok(Resp.ok("스케줄이 성공적으로 삭제되었습니다."));
    }
}
