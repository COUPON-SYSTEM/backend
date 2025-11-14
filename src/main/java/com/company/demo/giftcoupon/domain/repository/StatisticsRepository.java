package com.company.demo.giftcoupon.domain.repository;

import com.company.demo.common.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
@RequiredArgsConstructor
@Slf4j
public class StatisticsRepository {

    private final RedisUtil redisUtil;

    // 증가

    public void incrementIssuedCount(String eventId) {
        String key = redisUtil.getIssuedCountKey(eventId);
        redisUtil.incrementValue(key, 1);
    }

    public void incrementEstimatedRevenue(String eventId, Long couponValue) {
        String key = redisUtil.getEstimatedRevenueKey(eventId);
        redisUtil.incrementValue(key, couponValue);
    }

    public void incrementGenderCount(String eventId, String gender) {
        String key = redisUtil.getGenderCountsKey(eventId);
        redisUtil.incrementHashField(key, gender, 1);
    }

    public void incrementAgeGroupCount(String eventId, String ageGroup) {
        String key = redisUtil.getAgeGroupCountsKey(eventId);
        redisUtil.incrementHashField(key, ageGroup, 1);
    }

    // 조회

    public Long getIssuedCount(String eventId) {
        String key = redisUtil.getIssuedCountKey(eventId);
        Object value = redisUtil.getValue(key);
        //TODO: RedisUtil이 반환한 Object를 안전하게 Long으로 변환 (Integer/String 방지 로직 필요)
        return value == null ? 0L : Long.valueOf(value.toString());
    }

    public Long getEstimatedRevenue(String eventId) {
        String key = redisUtil.getEstimatedRevenueKey(eventId);
        Object value = redisUtil.getValue(key);
        return value == null ? 0L : Long.valueOf(value.toString());
    }

    public Map<String, Long> getGenderCounts(String eventId) {
        String key = redisUtil.getGenderCountsKey(eventId);
        return redisUtil.getHashEntries(key);
    }

    public Map<String, Long> getAgeGroupCounts(String eventId) {
        String key = redisUtil.getAgeGroupCountsKey(eventId);
        return redisUtil.getHashEntries(key);
    }

    public Double getVisitTrendChange(Long publisherId) {
        String key = redisUtil.getVisitTrendChangeKey(publisherId);
        Object value = redisUtil.getValue(key);

        if (value == null) return 0.0;

        try {
            return Double.valueOf(value.toString());
        } catch (NumberFormatException e) {
            log.warn("방문 추이 값 형식이 잘못되었습니다. PublisherId: {}", publisherId);
            return 0.0;
        }
    }
}