package com.example.hello_friends.schedule.application.serivce;

import com.example.hello_friends.schedule.application.request.ReservationRequest;
import com.example.hello_friends.schedule.application.response.ReservationResponse;
import com.example.hello_friends.schedule.domain.Reservation;
import com.example.hello_friends.schedule.domain.ReservationRepository;
import com.example.hello_friends.schedule.domain.Schedule;
import com.example.hello_friends.schedule.domain.ScheduleRepository;
import com.example.hello_friends.user.domain.User;
import com.example.hello_friends.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    // 스케줄을 지정된 인원수만큼 예약
    @Transactional
    public ReservationResponse createReservation(Long userId, ReservationRequest reservationRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Schedule schedule = scheduleRepository.findById(reservationRequest.getScheduleId())
                .orElseThrow(() -> new IllegalArgumentException("예약할 스케줄을 찾을 수 없습니다."));

        // 예약하려는 인원을 포함했을 때, 총 정원을 초과하는지 확인합니다.
        if (schedule.isFull(reservationRequest.getQuantity())) {
            throw new IllegalStateException("정원이 마감되어 예약할 수 없습니다. 남은 자리: " + (schedule.getMaxHeadcount() - schedule.getCurrentHeadcount()));
        }

        if (reservationRepository.existsByUserAndSchedule(user, schedule)) {
            throw new IllegalStateException("이미 해당 날짜에 예약을 완료했습니다. 중복 예약은 불가능합니다.");
        }

        //  예약이 확정시 스케줄의 '현재 예약된 인원' 수를 업데이트
        schedule.addReservation(reservationRequest.getQuantity());

        Reservation reservation = new Reservation(user, schedule, reservationRequest.getQuantity());
        Reservation savedReservation = reservationRepository.save(reservation);

        return ReservationResponse.from(savedReservation);
    }

    // 예약 취소
    @Transactional
    public void cancelReservation(Long userId, Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다."));

        // 예약을 취소하려는 사용자가 실제 예약자와 동일 인물인지 확인합니다.
        if (!reservation.getUser().getId().equals(userId)) {
            try {
                throw new AccessDeniedException("예약을 취소할 권한이 없습니다.");
            } catch (AccessDeniedException e) {
                throw new RuntimeException(e);
            }
        }

        // 예약 취소시, 연결된 스케줄의 '현재 예약된 인원' 수를 다시 감소
        Schedule schedule = reservation.getSchedule();
        schedule.cancelReservation(reservation.getQuantity());

        reservationRepository.delete(reservation);
    }

    // 사용자의 모든 예약 내역 조회
    @Transactional(readOnly = true)
    public List<ReservationResponse> getMyReservations(Long userId) {
        List<Reservation> reservations = reservationRepository.findAllByUserId(userId);

        return reservations.stream()
                .map(ReservationResponse::from)
                .collect(Collectors.toList());
    }
}
