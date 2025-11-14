package com.example.hello_friends.user.application.service;

import com.example.hello_friends.common.entity.EntityState;
import com.example.hello_friends.notification.application.service.NotificationService;
import com.example.hello_friends.user.domain.*; // User, UserRepository 등 임포트
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class BlackUserService {

    private final BlackUserRepository blackUserRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

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
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void addWarningAndBlacklistIfNeeded(Long userId, String reason, String adminMemo) {
        User user = userRepository.findByIdAndState(userId, EntityState.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("경고를 줄 사용자를 찾을 수 없음: id=" + userId));

        BlackUser blackUser = blackUserRepository.findByUserAndState(user, EntityState.ACTIVE)
                .orElse(null);

        // 이동할 페이지 URL (예: 마이페이지 내 제재 내역)
        String notificationUrl = "/my-page/sanctions";

        if (blackUser == null) {
            log.info("첫 경고 발생: userId={}, reason={}", userId, reason);
            BlackUser newWarning = new BlackUser(user, reason, LocalDateTime.now().plusYears(1));
            if (adminMemo != null && !adminMemo.isBlank()) {
                newWarning.addAdminMemo(adminMemo);
            }
            blackUserRepository.save(newWarning);

            String notificationContent = "서비스 정책 위반으로 경고 1회가 부여되었습니다. 3회 누적 시 이용이 제한될 수 있습니다.";
            notificationService.send(user, notificationContent, notificationUrl);

        } else {
            log.info("추가 경고 발생: userId={}, 현재 경고 횟수={}", userId, blackUser.getWarningCount());
            blackUser.updateWarningCount();
            blackUser.addAdminMemo("추가 경고: " + reason);

            String warningContent = String.format(
                    "서비스 정책 위반으로 경고가 추가 부여되었습니다. (현재 경고: %d회)",
                    blackUser.getWarningCount()
            );
            notificationService.send(user, warningContent, notificationUrl);

            if (blackUser.getWarningCount() >= WARNING_THRESHOLD) {
                log.warn("경고 3회 누적으로 블랙리스트 처리: userId={}", userId);
                applyBlacklist(user, blackUser);

                String blacklistContent = String.format(
                        "경고 %d회 누적으로 계정이 블랙리스트 처리되어 %d일간 서비스 이용이 제한됩니다.",
                        WARNING_THRESHOLD,
                        BLACKLIST_DURATION_DAYS
                );
                notificationService.send(user, blacklistContent, notificationUrl);
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