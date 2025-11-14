package com.example.hello_friends.schedule.presentation;

import com.example.hello_friends.common.response.Resp;
import com.example.hello_friends.schedule.application.request.ReservationRequest;
import com.example.hello_friends.schedule.application.response.ReservationResponse;
import com.example.hello_friends.schedule.application.serivce.ReservationService;
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
@Tag(name = "예약 관리")
public class ReservationController {
    private final ReservationService reservationService;

    @Operation(summary = "예약하기", description = "특정 스케줄(날짜 옵션)을 지정된 인원수만큼 예약합니다.")
    @PostMapping("/api/user/reservation")
    public ResponseEntity<Resp<ReservationResponse>> createReservation(
            @Parameter(hidden = true) @Auth JwtPrincipalDto jwtPrincipalDto,
            @RequestBody ReservationRequest reservationRequest
    ) {
        ReservationResponse reservationResponse = reservationService.createReservation(jwtPrincipalDto.getId(), reservationRequest);
        return ResponseEntity.ok(Resp.ok(reservationResponse));
    }

    @Operation(summary = "내 예약 목록 조회", description = "현재 로그인한 사용자의 모든 예약 내역을 조회합니다.")
    @GetMapping("/api/user/reservation/list")
    public ResponseEntity<Resp<List<ReservationResponse>>> getMyReservations(
            @Parameter(hidden = true) @Auth JwtPrincipalDto jwtPrincipalDto
    ) {
        List<ReservationResponse> myReservations = reservationService.getMyReservations(jwtPrincipalDto.getId());
        return ResponseEntity.ok(Resp.ok(myReservations));
    }

    @Operation(summary = "예약 취소", description = "자신의 예약 내역 중 특정 예약을 취소합니다.")
    @DeleteMapping("/api/user/reservations/{reservationId}")
    public ResponseEntity<Resp<String>> cancelReservation(
            @Parameter(hidden = true) @Auth JwtPrincipalDto jwtPrincipalDto,
            @Parameter(description = "취소할 예약의 ID") @PathVariable Long reservationId
    ) {
        reservationService.cancelReservation(jwtPrincipalDto.getId(), reservationId);
        return ResponseEntity.ok(Resp.ok("예약이 성공적으로 취소되었습니다."));
    }
}
