package com.example.hello_friends.redis.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class QueueService {
    private final StringRedisTemplate redisTemplate;
    // 대기줄
    private static final String WAITING_KEY = "waiting_queue";
    // 입장권 가진 사람 목록
    private static final String ACTIVE_KEY = "active_queue";

    // 대기열 등록 - 번호표 발금
    public void registerQueue(String userId) {
        long time = System.currentTimeMillis();
        redisTemplate.opsForZSet().add(WAITING_KEY, userId, time);
    }

    // 내 순서 확인
    public Long getRank(String userId) {
        return redisTemplate.opsForZSet().rank(WAITING_KEY, userId);
    }

    // 입장 허용 - 1초마다 50명씩
    @Scheduled(fixedDelay = 1000)
    public void allowUsers() {
        // 대기열 맨 앞 50명 가져오기
        Set<String> users = redisTemplate.opsForZSet().range(WAITING_KEY, 0, 49);

        if (users != null && !users.isEmpty()) {
            // 참가열로 이동
            redisTemplate.opsForSet().add(ACTIVE_KEY, users.toArray(new String[0]));
            // 대기열에서 삭제
            redisTemplate.opsForZSet().remove(WAITING_KEY, users.toArray());
        }
    }

    // 입장 권한 확인
    public boolean isAllowed(String userId) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(ACTIVE_KEY, userId));
    }

    // 퇴장 처리
    public void removeUser(String userId) {
        redisTemplate.opsForSet().remove(ACTIVE_KEY, userId);
    }
}
