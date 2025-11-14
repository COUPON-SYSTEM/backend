package com.company.demo.giftcoupon.config.queue;

import com.company.demo.common.constant.RedisKey;
import com.company.demo.common.response.error.ErrorCode;
import com.company.demo.giftcoupon.exception.CouponIssueException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Arrays;

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
    public void tryPush(String userId, String promotionId) { // TODO: addUserToQueue 이름의 명확화, void말고 enum을 사용해서 결과 상태 반환 고려
        try {
            String value = userId + ":" + promotionId;
            Long result = redisTemplate.execute(
                    pushIfUnderLimitScript,
                    Arrays.asList(
                            RedisKey.COUPON_REQUEST_QUEUE_KEY,   // KEYS[1]
                            RedisKey.COUPON_TOTAL_COUNT_KEY,     // KEYS[2]
                            RedisKey.COUPON_USER_GUARD_PREFIX    // KEYS[3]
                    ),
                    String.valueOf(MAX_QUEUE_SIZE),                    // ARGV[1]: maxTotal
                    value                 // ARGV[2]:  userId + promotionId 문자열
            );
            log.info("Lua KEYS={}, ARGV={}",
                    Arrays.asList(
                            RedisKey.COUPON_REQUEST_QUEUE_KEY,
                            RedisKey.COUPON_TOTAL_COUNT_KEY,
                            RedisKey.COUPON_USER_GUARD_PREFIX),
                    Arrays.asList(
                            String.valueOf(MAX_QUEUE_SIZE),
                            String.valueOf(userId)));

            log.info("Lua result={}", result);

            Long qlen = redisTemplate.opsForList().size("coupon:request:queue");
            String cnt = redisTemplate.opsForValue().get("coupon:request:count");
            String guard = redisTemplate.opsForValue().get("coupon:user:" + userId);
            log.info("AFTER => r={}, LLEN={}, CNT={}, GUARD({})={}", result, qlen, cnt, userId, guard);

            // Lua 스크립트 반환값에 따라 분기
            if (Long.valueOf(1).equals(result)) {
                return; // 성공
            } else if (Long.valueOf(0).equals(result)) {
                // 100개 초과로 인해 push 실패한 경우
                throw new CouponIssueException(ErrorCode.COUPON_ISSUANCE_CLOSED);
            } else if (Long.valueOf(-1).equals(result)) {
                throw new CouponIssueException(ErrorCode.DUPLICATE_USER_REQUEST); // 중복 유저
            } else {
                // Lua 스크립트가 예상치 못한 값을 반환한 경우
                log.error("Unexpected script result: {}", result);
                throw new CouponIssueException(ErrorCode.COUPON_REDIS_FAILED);
            }
        } catch (DataAccessException e) {
            // Redis 연결 실패, 스크립트 로드 실패 등 시스템 오류
            log.error("Redis operation failed", e);
            throw new CouponIssueException(ErrorCode.REDIS_CONNECTION_FAILED);
        }

    }
}
