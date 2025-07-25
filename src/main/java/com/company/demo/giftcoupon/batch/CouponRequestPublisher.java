package com.company.demo.giftcoupon.batch;

import com.company.demo.giftcoupon.event.CouponRequestEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponRequestPublisher {
    private final RedisTemplate<String, String> redisTemplate;
    private final KafkaTemplate<String, CouponRequestEvent> kafkaTemplate;
    private final CouponIssuedRepository couponIssuedRepository;

    /**
     Redis List (coupon:queue)에서 앞에서부터 10개를 꺼내고,
     그 10개를 큐에서 제거한 뒤,
     그 결과(꺼낸 10개)를 리턴
     **/
    private static final String LUA_POP_10 = """
        local items = redis.call('LRANGE', KEYS[1], 0, 9)
        redis.call('LTRIM', KEYS[1], 10, -1)
        return items
    """;

    // Redis에서 누적 발급 수를 저장하는 키
    private static final String ISSUED_COUNT_KEY = "coupon:issued:count";

    private static final long MAX_TOTAL = 100;

    // 3초마다 실행
    @Scheduled(fixedDelay = 3000)
    public void issueCoupons() {
        // 현재까지 발급된 수 조회
        Long issuedCount = Long.parseLong(redisTemplate.opsForValue()
                .getOrDefault(ISSUED_COUNT_KEY, "0"));

        // 발급 수가 이미 한계치를 넘었다면 중단
        if (issuedCount >= MAX_TOTAL) {
            log.info("쿠폰 발급 종료됨");
            return;
        }

        // Redis 큐에서 최대 10명 꺼내기
        List<String> users = redisTemplate.execute(
                new DefaultRedisScript<>(LUA_POP_10, List.class),
                Collections.singletonList("coupon:queue")
        );

        if (users == null || users.isEmpty()) return;

        // 남은 수량보다 많이 꺼낸 경우 잘라냄 (예: 95명 발급된 상태에서 10명 꺼내면 → 5명만 처리)
        long limit = MAX_TOTAL - issuedCount;
        if (users.size() > limit) {
            users = users.subList(0, (int) limit);
        }

        List<CouponIssued> issued = new ArrayList<>();
        for (String userId : users) {
            // Kafka 발행
            kafkaTemplate.send("coupon-topic", new CouponIssuedEvent(userId));
            // DB 저장용 객체 생성
            issued.add(new CouponIssued(userId, LocalDateTime.now()));
        }

        // DB에 저장
        repository.saveAll(issued);

        // Redis에 누적 발급 수 업데이트
        redisTemplate.opsForValue().increment(ISSUED_COUNT_KEY, issued.size());

        log.info("✅ {}명 발급 완료 (누적 {})", issued.size(), issuedCount + issued.size());
    }
}
