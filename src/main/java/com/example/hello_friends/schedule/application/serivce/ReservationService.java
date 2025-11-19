package com.example.hello_friends.schedule.application.serivce;

import com.example.hello_friends.common.exception.NoAuthorityException;
import com.example.hello_friends.common.exception.ReservationNotFoundException;
import com.example.hello_friends.common.exception.UserNotFoundException;
import com.example.hello_friends.common.response.MotherException;
import com.example.hello_friends.notification.application.service.NotificationService;
import com.example.hello_friends.schedule.application.request.ReservationRequest;
import com.example.hello_friends.schedule.application.response.ReservationResponse;
import com.example.hello_friends.schedule.domain.*;
import com.example.hello_friends.user.domain.User;
import com.example.hello_friends.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    // 스케줄을 지정된 인원수만큼 예약
    @Transactional
    public ReservationResponse createReservation(Long userId, ReservationRequest reservationRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다. ID : " + userId));
        Schedule schedule = scheduleRepository.findById(reservationRequest.getScheduleId())
                .orElseThrow(() -> new ReservationNotFoundException("예약할 스케줄을 찾을 수 없습니다. ID : " + reservationRequest.getScheduleId()));

        if (schedule.isFull(reservationRequest.getQuantity())) {
            throw new MotherException("정원이 마감되어 예약할 수 없습니다. 남은 자리: " + (schedule.getMaxHeadcount() - schedule.getCurrentHeadcount()), HttpStatus.BAD_REQUEST);
        }

        if (reservationRepository.existsByUserAndScheduleAndStatus(user, schedule, ReservationStatus.RESERVED)) {
            throw new MotherException("이미 해당 날짜에 예약을 완료했습니다. 중복 예약은 불가능합니다.", HttpStatus.BAD_REQUEST);
        }

        schedule.addReservation(reservationRequest.getQuantity());

        Reservation reservation = new Reservation(user, schedule, reservationRequest.getQuantity());
        Reservation savedReservation = reservationRepository.save(reservation);

        return ReservationResponse.from(savedReservation);
    }

    // 사용자 직접 예약 취소
    @Transactional
    public void cancelReservation(Long userId, Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("존재하지 않는 예약입니다. ID : " + reservationId));

        if (!reservation.getUser().getId().equals(userId)) {
            throw new NoAuthorityException("예약을 취소할 권한이 없습니다.");
        }

        Schedule schedule = reservation.getSchedule();
        schedule.cancelReservation(reservation.getQuantity());

        reservation.cancel();
    }

    // 사용자의 모든 예약 내역 조회
    @Transactional(readOnly = true)
    public List<ReservationResponse> getMyReservations(Long userId, String type) {
        List<Reservation> reservations;

        // 예약된 것만
        if ("RESERVED".equalsIgnoreCase(type)) {
            reservations = reservationRepository.findAllByUserIdAndStatus(userId, ReservationStatus.RESERVED);
        }
        // 취소된 것만
        else if ("CANCELED".equalsIgnoreCase(type)) {
            reservations = reservationRepository.findAllByUserIdAndStatus(userId, ReservationStatus.CANCELED);
        }
        // type이 없거나 전부 다
        else {
            reservations = reservationRepository.findAllByUserId(userId);
        }

        return reservations.stream()
                .map(ReservationResponse::from)
                .collect(Collectors.toList());
    }

    // 판매자나 관리자가 예약 강제 취소
    @Transactional
    @PreAuthorize("hasAnyRole('ROLE_SELLER', 'ROLE_ADMIN')")
    public void forceCancelReservation(Long userId, Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("존재하지 않는 예약입니다. ID : " + reservationId));

        User customer = reservation.getUser();

        String scheduleName = reservation.getSchedule().getBoard().getTitle();
        String notificationContent = String.format("%s 예약이 판매자 사정으로 취소되었습니다.", scheduleName);

        notificationService.send(customer, notificationContent);

        Schedule schedule = reservation.getSchedule();
        schedule.cancelReservation(reservation.getQuantity());

        reservation.cancel();
    }
}