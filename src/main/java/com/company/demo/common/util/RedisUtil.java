package com.company.demo.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;

    // --- Redis Key 정의 ---
    public String getIssuedCountKey(Long couponId) {
        return "stats:coupon:" + couponId + ":issued_count";
    }

    public String getEstimatedRevenueKey(Long couponId) {
        return "stats:coupon:" + couponId + ":estimated_revenue";
    }

    public String getGenderCountsKey(Long couponId) {
        return "stats:coupon:" + couponId + ":gender_counts";
    }

    public String getAgeGroupCountsKey(Long couponId) {
        return "stats:coupon:" + couponId + ":age_counts";
    }

    public String getVisitTrendChangeKey(Long publisherId) {
        return "stats:publisher:" + publisherId + ":visit_trend";
    }

    /**
     * Long 타입 카운터를 원자적으로 1 증가
     */
    public void incrementValue(String key, long delta) {
        redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * String (Long/Double) 값 조횐
     */
    public Object getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * Hash 구조 내 특정 필드의 카운터를 원자적으로 1 증가
     */
    public void incrementHashField(String key, String field, long delta) {
        redisTemplate.opsForHash().increment(key, field, delta);
    }

    /**
     * Hash 구조 전체를 조회
     */
    public Map<String, Long> getHashEntries(String key) {
        Map<Object, Object> rawMap = redisTemplate.opsForHash().entries(key);

        Map<String, Long> result = new HashMap<>();
        for (Map.Entry<Object, Object> entry : rawMap.entrySet()) {
            // RedisTemplate<String, Object>를 사용했으므로 Object를 Long으로 캐스팅 시도
            Object value = entry.getValue();
            Long longValue = (value instanceof Long) ? (Long) value : Long.valueOf(value.toString());

            result.put(entry.getKey().toString(), longValue);
        }
        return result;
    }
}
