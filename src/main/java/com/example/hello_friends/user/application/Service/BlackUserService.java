package com.example.hello_friends.user.application.Service;

import com.example.hello_friends.common.entity.EntityState;
import com.example.hello_friends.user.domain.*; // User, UserRepository 등 임포트
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class BlackUserService {

    private final BlackUserRepository blackUserRepository;
    private final UserRepository userRepository; // User 정보를 가져오기 위해 추가

    // 경고 횟수 기준 (3회)
    private static final int WARNING_THRESHOLD = 3;
    // 블랙리스트 처리 시 제재 기간 (30일)
    private static final long BLACKLIST_DURATION_DAYS = 30;

    // 회원가입 시 이메일, 전화번호가 블랙리스트에 있는지 검증하는 메소드
    public void validateUserRegistration(String email, String phone) {
        if (blackUserRepository.existsByUser_EmailAndState(email, EntityState.ACTIVE)) {
            log.warn("블랙리스트에 등록된 이메일: email={}", email);
            throw new IllegalArgumentException("해당 이메일은 블랙리스트에 등록되어 있어 가입이 제한됩니다.");
        }

        if (blackUserRepository.existsByUser_PhoneAndState(phone, EntityState.ACTIVE)) {
            log.warn("블랙리스트에 등록된 전화번호: phone={}", phone);
            throw new IllegalArgumentException("해당 전화번호는 블랙리스트에 등록되어 있어 가입이 제한됩니다.");
        }
    }

    // 사용자에게 경고를 1회 추가하고, 경고 횟수가 3회 이상이면 블랙리스트로 처리하는 메소드
    @Transactional
    public void addWarningAndBlacklistIfNeeded(Long userId, String reason) {
        User user = userRepository.findByIdAndState(userId, EntityState.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("경고를 줄 사용자를 찾을 수 없음: id=" + userId));

        // 해당 사용자가 이미 경고를 받은 기록이 있는지 확인
        BlackUser blackUser = blackUserRepository.findByUserAndState(user, EntityState.ACTIVE)
                .orElse(null); // 없으면 null

        if (blackUser == null) {
            log.info("첫 경고 발생: userId={}, reason={}", userId, reason);
            // 첫 경고 시에는 제재 종료일이 의미 없으므로 임의의 값 삽입
            BlackUser newWarning = new BlackUser(user, reason, LocalDateTime.now().plusYears(1));
            blackUserRepository.save(newWarning);
        } else {
            // 이미 경고 기록이 있는 경우 경고 횟수를 1 증가
            log.info("추가 경고 발생: userId={}, 현재 경고 횟수={}", userId, blackUser.getWarningCount());
            blackUser.updateWarningCount();
            blackUser.addAdminMemo("추가 경고: " + reason);

            // 3회 넘는경우
            if (blackUser.getWarningCount() >= WARNING_THRESHOLD) {
                log.warn("경고 3회 누적으로 블랙리스트 처리: userId={}", userId);
                applyBlacklist(user, blackUser);
            }
        }
    }

    // 실제 사용자를 블랙리스트로 처리하는 내부 로직
    private void applyBlacklist(User user, BlackUser blackUser) {
        // 사용자의 상태를 DELETE로 변경하여 서비스 이용을 막음
        user.delete();
        // 제재 기간을 설정 (지금부터 30일 후)
        blackUser.updateBlackEndDate(LocalDateTime.now().plusDays(BLACKLIST_DURATION_DAYS));
        // 관리자 메모 추가
        blackUser.addAdminMemo("경고 " + WARNING_THRESHOLD + "회 누적으로 자동 블랙리스트 처리됨");
    }
}