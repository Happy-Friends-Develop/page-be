package com.example.hello_friends.schedule.application.serivce;

import com.example.hello_friends.board.domain.Board;
import com.example.hello_friends.board.domain.BoardRepository;
import com.example.hello_friends.schedule.application.request.ScheduleRequest;
import com.example.hello_friends.schedule.application.request.ScheduleUpdateRequest;
import com.example.hello_friends.schedule.application.response.ScheduleResponse;
import com.example.hello_friends.schedule.domain.Schedule;
import com.example.hello_friends.schedule.domain.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final BoardRepository boardRepository;

    // 게시글에 스케줄 추가
    @Transactional
    @PreAuthorize("hasAnyRole('ROLE_SELLER', 'ROLE_ADMIN')")
    public void addSchedulesToBoard(Long boardId, List<ScheduleRequest> requests) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        List<Schedule> schedules = requests.stream()
                .map(request -> new Schedule(
                        board,
                        request.getScheduleDate(),
                        request.getMaxHeadcount(),
                        request.getPrice()
                ))
                .collect(Collectors.toList());

        scheduleRepository.saveAll(schedules);
    }

    // 게시글에 붙은 스케줄 목록 조회
    @Transactional(readOnly = true)
    public List<ScheduleResponse> getSchedulesForBoard(Long boardId) {
        List<Schedule> schedules = scheduleRepository.findAllByBoardId(boardId);

        return schedules.stream()
                .map(ScheduleResponse::from)
                .collect(Collectors.toList());
    }

    // 스케줄 수정
    @Transactional
    @PreAuthorize("hasAnyRole('ROLE_SELLER', 'ROLE_ADMIN')")
    public void updateSchedule(Long boardId, Long scheduleId, Long sellerId, ScheduleUpdateRequest request) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("스케줄을 찾을 수 없습니다."));

        // 권한 검증
        if (!schedule.getBoard().getId().equals(boardId) || !schedule.getBoard().getUser().getId().equals(sellerId)) {
            throw new SecurityException("스케줄을 수정할 권한이 없습니다.");
        }

        // 예외처리
        if (request.getMaxHeadcount() < schedule.getCurrentHeadcount()) {
            throw new IllegalStateException("최대 정원은 현재 예약된 인원(" + schedule.getCurrentHeadcount() + "명)보다 적을 수 없습니다.");
        }

        schedule.update(request.getScheduleDate(), request.getMaxHeadcount(), request.getPrice());
    }

    // 스케줄 삭제
    @Transactional
    @PreAuthorize("hasAnyRole('ROLE_SELLER', 'ROLE_ADMIN')")
    public void deleteSchedule(Long boardId, Long scheduleId, Long sellerId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("스케줄을 찾을 수 없습니다."));

        if (!schedule.getBoard().getId().equals(boardId) || !schedule.getBoard().getUser().getId().equals(sellerId)) {
            throw new SecurityException("스케줄을 삭제할 권한이 없습니다.");
        }

        if (schedule.getCurrentHeadcount() > 0) {
            throw new IllegalStateException("이미 예약자가 있는 스케줄은 삭제할 수 없습니다. 예약을 먼저 취소해주세요.");
        }

        scheduleRepository.delete(schedule);
    }

}
