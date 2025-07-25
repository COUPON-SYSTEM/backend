package com.company.demo.giftcoupon.queue;

import com.company.demo.giftcoupon.producer.CustomKafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.company.demo.giftcoupon.event.CouponRequestEvent;

import java.util.Collections;

import static com.company.demo.common.constant.RedisKey.COUPON_REQUEST_QUEUE_KEY;

// 요청을 Redis에서 10개씩 뽑아오는 Component
@Slf4j
@Component
@RequiredArgsConstructor
public class CouponRequestRedisQueue {

     private final RedisTemplate<String, String> redisTemplate;
     private final CustomKafkaProducer customKafkaProducer;

    private static final String LUA_PUSH_IF_UNDER_100 = """
        local currentLength = redis.call('LLEN', KEYS[1])
        if tonumber(currentLength) < tonumber(ARGV[1]) then
            redis.call('RPUSH', KEYS[1], ARGV[2])
            return 1
        else
            return 0
        end
    """;

    public boolean tryPush(String userId) {
        Long result = redisTemplate.execute(
                new DefaultRedisScript<>(LUA_PUSH_IF_UNDER_100, Long.class),
                Collections.singletonList(COUPON_REQUEST_QUEUE_KEY),
                "100", userId
        );
        return result != null && result == 1;
    }

    /* Redis 큐에서 최대 10개의 요청을 꺼내 처리
    @Scheduled(fixedDelay = 1000) // 1초 간격으로 반복 실행
    public void popFromQueueAndSendToKafka() {
        for (int i = 0; i < 10; i++) {
            // Redis 큐(List)에서 데이터를 하나 꺼냄 (오른쪽 Pop)
            String userId = redisTemplate.opsForList().rightPop(COUPON_QUEUE_KEY);

            // 큐가 비어있으면 루프 중단
            if (userId == null) break;

            // Kafka로 메시지 전송
            CouponRequestEvent event = new CouponRequestEvent(userId);
            customKafkaProducer.sendRequestMessage(event);
            log.info("Gift request sent: #{}", i);
        }
    }*/
}
