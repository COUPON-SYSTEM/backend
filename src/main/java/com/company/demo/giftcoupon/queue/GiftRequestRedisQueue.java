package com.company.demo.giftcoupon.queue;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

// 요청을 Redis에서 10개씩 뽑아오는 Component
@Component
@RequiredArgsConstructor
public class GiftRequestRedisQueue {

     private final RedisTemplate<String, String> redisTemplate;
     private final GiftRequestProducer giftRequestProducer;

    // Redis 리스트(queue)의 키 – 사용자 요청들이 쌓이는 큐의 이름
    private static final String COUPON_QUEUE_KEY = "coupon:request:queue";

    // Redis 큐에서 최대 10개의 요청을 꺼내 처리
    @Scheduled(fixedDelay = 1000) // 1초 간격으로 반복 실행
    public void popFromQueueAndSendToKafka() {
        for (int i = 0; i < 10; i++) {
            // Redis 큐(List)에서 데이터를 하나 꺼냄 (오른쪽 Pop)
            String userId = redisTemplate.opsForList().rightPop(COUPON_QUEUE_KEY);

            // 큐가 비어있으면 루프 중단
            if (userId == null) break;

            // Kafka로 메시지 전송
            GiftRequestEvent event = new GiftRequestEvent(userId);
            giftRequestProducer.send(event);
        }
    }
}
