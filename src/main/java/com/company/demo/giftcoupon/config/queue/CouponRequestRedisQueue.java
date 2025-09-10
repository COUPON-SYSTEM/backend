package com.company.demo.giftcoupon.config.queue;

import com.company.demo.common.constant.RedisKey;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;

// 요청을 Redis에서 10개씩 뽑아오는 Component
@Slf4j
@Component
@RequiredArgsConstructor
public class CouponRequestRedisQueue {

    private final RedisTemplate<String, String> redisTemplate;
    private static final int MAX_QUEUE_SIZE = 100;
    private static final String LUA_SCRIPT_PATH = "scripts/push_if_under_limit.lua";
    private DefaultRedisScript<Long> pushIfUnderLimitScript;

    @PostConstruct
    public void initScript() {
        pushIfUnderLimitScript = new DefaultRedisScript<>();
        pushIfUnderLimitScript.setLocation(new ClassPathResource(LUA_SCRIPT_PATH));
        pushIfUnderLimitScript.setResultType(Long.class);
    }

    /**
     * 큐의 크기가 MAX_QUEUE_SIZE 미만이면 userId를 push
     * @param userId 유저 아이디
     * @return 성공 여부
     */
    public boolean tryPush(String userId) {
        Long result = redisTemplate.execute(
                pushIfUnderLimitScript,
                Collections.singletonList(RedisKey.COUPON_REQUEST_QUEUE_KEY),
                String.valueOf(MAX_QUEUE_SIZE), userId
        );
        return Long.valueOf(1).equals(result);
    }
}
