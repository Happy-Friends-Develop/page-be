package com.example.hello_friends.event.presentation;

import com.example.hello_friends.common.response.Resp;
import com.example.hello_friends.event.application.EventType;
import com.example.hello_friends.event.application.request.EventCreateRequest;
import com.example.hello_friends.event.application.response.EventResponse;
import com.example.hello_friends.event.application.service.EventService;
import com.example.hello_friends.security.annotation.Auth;
import com.example.hello_friends.security.filter.JwtPrincipalDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "이벤트 & 공지사항")
public class EventController {
    private final EventService eventService;

    @Operation(summary = "이벤트 생성", description = "관리자가 새로운 이벤트를 생성합니다.")
    @PostMapping("/api/admin/events")
    public Resp<EventResponse> createEvent(
            @RequestBody EventCreateRequest request,
            @Parameter(hidden = true) @Auth JwtPrincipalDto jwtPrincipalDto) {
        EventResponse response = eventService.createEvent(request, jwtPrincipalDto.getId(), EventType.EVENT);
        return Resp.ok(response);
    }

    @Operation(summary = "공지사항 생성", description = "관리자가 새로운 공지사항을 생성합니다.")
    @PostMapping("/api/admin/notices")
    public Resp<EventResponse> createNotice(
            @RequestBody EventCreateRequest request,
            @Parameter(hidden = true) @Auth JwtPrincipalDto jwtPrincipalDto) {
        EventResponse response = eventService.createEvent(request, jwtPrincipalDto.getId(), EventType.NOTICE);
        return Resp.ok(response);
    }

    @Operation(summary = "이벤트/공지사항 목록 조회", description = "모든 이벤트와 공지사항 목록을 조회합니다. type 파라미터로 필터링할 수 있습니다.")
    @GetMapping("/api/user/events")
    public Resp<List<EventResponse>> getAllEvents(
            @Parameter(description = "필터링할 타입 (EVENT 또는 NOTICE)")
            @RequestParam(required = false) EventType type
    ) {
        return Resp.ok(eventService.getAllEvents(type));
    }

    @Operation(summary = "특정 이벤트/공지사항 조회", description = "ID로 특정 이벤트나 공지사항의 상세 정보를 조회합니다.")
    @GetMapping("/api/user/events/{eventId}")
    public Resp<EventResponse> getEventById(@PathVariable Long eventId) {
        return Resp.ok(eventService.getEventById(eventId));
    }

    @Operation(summary = "이벤트 수정", description = "이벤트를 수정합니다.")
    @PutMapping("/api/admin/events/{eventId}")
    public Resp<EventResponse> updateEvent(
            @PathVariable Long eventId,
            @RequestBody EventCreateRequest eventCreateRequest){
        return Resp.ok(eventService.updateEvent(eventCreateRequest, eventId));
    }

    @Operation(summary = "이벤트 참가", description = "사용자가 이벤트에 참가 신청을 합니다.")
    @PostMapping("/api/user/events/{eventId}/join")
    public Resp<String> joinEvent(
            @PathVariable Long eventId,
            @Parameter(hidden = true) @Auth JwtPrincipalDto jwtPrincipalDto) {
        eventService.joinEvent(eventId, jwtPrincipalDto.getId());
        return Resp.ok("이벤트 참가 성공");
    }

    @Operation(summary = "이벤트 참가 취소", description = "사용자가 이벤트 참가를 취소합니다.")
    @DeleteMapping("/api/user/events/{eventId}/join")
    public Resp<String> cancelEventParticipation(
            @PathVariable Long eventId,
            @Parameter(hidden = true) @Auth JwtPrincipalDto jwtPrincipalDto) {
        eventService.cancelEventParticipation(eventId, jwtPrincipalDto.getId());
        return Resp.ok("이벤트 참가 취소 성공");
    }
}
