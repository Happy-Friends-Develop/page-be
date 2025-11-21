package com.example.hello_friends.event.presentation;

import com.example.hello_friends.common.response.MotherException;
import com.example.hello_friends.common.response.Resp;
import com.example.hello_friends.event.application.EventType;
import com.example.hello_friends.event.application.request.EventCreateRequest;
import com.example.hello_friends.event.application.response.EventResponse;
import com.example.hello_friends.event.application.service.EventService;
import com.example.hello_friends.redis.application.QueueService;
import com.example.hello_friends.security.annotation.Auth;
import com.example.hello_friends.security.filter.JwtPrincipalDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "이벤트 & 공지사항")
public class EventController {
    private final EventService eventService;
    private final QueueService queueService;

    @Operation(summary = "이벤트 생성", description = "관리자가 새로운 이벤트를 생성합니다.")
    @PostMapping("/api/admin/events")
    public ResponseEntity<Resp<EventResponse>> createEvent(
            @RequestBody EventCreateRequest request,
            @Parameter(hidden = true) @Auth JwtPrincipalDto jwtPrincipalDto) {
        EventResponse response = eventService.createEvent(request, jwtPrincipalDto.getId(), EventType.EVENT);
        return ResponseEntity.ok(Resp.ok(response));
    }

    @Operation(summary = "공지사항 생성", description = "관리자가 새로운 공지사항을 생성합니다.")
    @PostMapping("/api/admin/notices")
    public ResponseEntity<Resp<EventResponse>> createNotice(
            @RequestBody EventCreateRequest request,
            @Parameter(hidden = true) @Auth JwtPrincipalDto jwtPrincipalDto) {
        EventResponse response = eventService.createEvent(request, jwtPrincipalDto.getId(), EventType.NOTICE);
        return ResponseEntity.ok(Resp.ok(response));
    }

    @Operation(summary = "이벤트/공지사항 목록 조회", description = "모든 이벤트와 공지사항 목록을 조회합니다. type 파라미터로 필터링할 수 있습니다.")
    @GetMapping("/api/user/events")
    public ResponseEntity<Resp<List<EventResponse>>> getAllEvents(
            @Parameter(description = "필터링할 타입 (EVENT 또는 NOTICE)")
            @RequestParam(required = false) EventType type
    ) {
        return ResponseEntity.ok(Resp.ok(eventService.getAllEvents(type)));
    }

    @Operation(summary = "특정 이벤트/공지사항 조회", description = "ID로 특정 이벤트나 공지사항의 상세 정보를 조회합니다.")
    @GetMapping("/api/user/events/{eventId}")
    public ResponseEntity<Resp<EventResponse>> getEventById(@PathVariable Long eventId) {
        return ResponseEntity.ok(Resp.ok(eventService.getEventById(eventId)));
    }

    @Operation(summary = "이벤트 수정", description = "이벤트를 수정합니다.")
    @PutMapping("/api/admin/events/{eventId}")
    public ResponseEntity<Resp<EventResponse>> updateEvent(
            @PathVariable Long eventId,
            @RequestBody EventCreateRequest eventCreateRequest){
        return ResponseEntity.ok(Resp.ok(eventService.updateEvent(eventCreateRequest, eventId)));
    }

    @Operation(summary = "이벤트 참가", description = "사용자가 이벤트에 참가 신청을 합니다.")
    @PostMapping("/api/user/events/{eventId}/join")
    public ResponseEntity<Resp<String>> joinEvent(
            @PathVariable Long eventId,
            @Parameter(hidden = true) @Auth JwtPrincipalDto jwtPrincipalDto) {
        String userIdStr = String.valueOf(jwtPrincipalDto.getId());

        // 입장 권환 확인 (DB 연결 전)
        if(!queueService.isAllowed(userIdStr)){
            // 내 순서가 아니라면 대기열에 등록하고 돌려보냄
            queueService.registerQueue(userIdStr);
            Long rank = queueService.getRank(userIdStr);
            throw new MotherException("대기 중입니다. 순번 : " + (rank + 1), HttpStatus.TOO_MANY_REQUESTS);
        }

        // 권한이 있는 50명만 여기서부터 실행
        try {
            eventService.joinEvent(eventId, jwtPrincipalDto.getId());
            return ResponseEntity.ok(Resp.ok("이벤트 참가 성공"));
        } catch (Exception e) {
            throw new MotherException("이벤트 참가 중 오류가 발생했습니다.", HttpStatus.BAD_REQUEST);
        } finally {
            queueService.removeUser(userIdStr);
        }
    }

    @Operation(summary = "이벤트 참가 취소", description = "사용자가 이벤트 참가를 취소합니다.")
    @DeleteMapping("/api/user/events/{eventId}/join")
    public ResponseEntity<Resp<String>> cancelEventParticipation(
            @PathVariable Long eventId,
            @Parameter(hidden = true) @Auth JwtPrincipalDto jwtPrincipalDto) {
        eventService.cancelEventParticipation(eventId, jwtPrincipalDto.getId());
        return ResponseEntity.ok(Resp.ok("이벤트 참가 취소 성공"));
    }
}
