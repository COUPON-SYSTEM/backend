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

    private static final String ISSUED_COUNT_KEY = "coupon:issued:count";
    private static final long MAX_COUPON_LIMIT = 100L;

    @Scheduled(fixedDelay = 3000)
    public void publishBatchToKafka() {
        Long currentCount = redisTemplate.opsForValue().get(ISSUED_COUNT_KEY) != null
                ? Long.parseLong(redisTemplate.opsForValue().get(ISSUED_COUNT_KEY))
                : 0L;

        if (currentCount >= MAX_COUPON_LIMIT) {
            log.info("쿠폰 발급 마감 (누적 {}명)", currentCount);
            return;
        }

        List<String> users = redisTemplate.execute(
                new DefaultRedisScript<>(LUA_POP_10, List.class),
                Collections.singletonList("coupon:queue")
        );

        if (users == null || users.isEmpty()) return;

        // 남은 쿠폰 수만큼만 처리
        long available = MAX_COUPON_LIMIT - currentCount;
        if (users.size() > available) {
            users = users.subList(0, (int) available);
        }

        List<CouponIssued> issuedList = new ArrayList<>();

        for (String userId : users) {
            CouponRequestEvent event = new CouponRequestEvent(userId);
            kafkaTemplate.send("coupon-topic", event);
            issuedList.add(new CouponIssued(userId, LocalDateTime.now()));
        }

        couponIssuedRepository.saveAll(issuedList);

        // 카운터 증가
        redisTemplate.opsForValue().increment(ISSUED_COUNT_KEY, users.size());

        log.info("Kafka 발행 완료: {}명, 누적 발급 수: {}", users.size(), currentCount + users.size());
    }
}
