package com.company.demo.giftcoupon.queue;

import com.company.demo.giftcoupon.config.queue.CouponRequestRedisQueue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class CouponRequestRedisQueueTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private CouponRequestRedisQueue couponRequestRedisQueue;

    private static final String COUPON_QUEUE_KEY = "coupon:queue"; // 큐에 들어갈 쿠폰

    @BeforeEach
    void setUp() {
        // Given: Redis 큐 초기화 및 테스트 데이터 10개 삽입
        redisTemplate.delete(COUPON_QUEUE_KEY);
        for (int i = 1; i <= 10; i++) {
            redisTemplate.opsForList().leftPush(COUPON_QUEUE_KEY, "testUser" + i);
        }
    }

//    @Test
//    @DisplayName("10개 저장되어 있는 거 뽑기")
//    void shouldPop10UsersFromRedisQueue() {
//        // Given: Redis 큐에 10개 데이터가 쌓여 있음
//        Long initialSize = redisTemplate.opsForList().size(COUPON_QUEUE_KEY);
//        assertThat(initialSize).isEqualTo(10);
//
//        // When: GiftIssueWorker가 큐를 처리
//        couponRequestRedisQueue.popFromQueueAndSendToKafka();
//
//        // Then: Redis 큐에는 더 이상 데이터가 없어야 함
//        Long remainingSize = redisTemplate.opsForList().size(COUPON_QUEUE_KEY);
//        assertThat(remainingSize).isEqualTo(0);
//    }
}
