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

    public void incrementIssuedCount(Long promotionId) {
        String key = redisUtil.getIssuedCountKey(promotionId);
        redisUtil.incrementValue(key, 1);
    }

    public void incrementEstimatedRevenue(Long promotionId, Long couponValue) {
        String key = redisUtil.getEstimatedRevenueKey(promotionId);
        redisUtil.incrementValue(key, couponValue);
    }

    public void incrementGenderCount(Long promotionId, String gender) {
        String key = redisUtil.getGenderCountsKey(promotionId);
        redisUtil.incrementHashField(key, gender, 1);
    }

    public void incrementAgeGroupCount(Long promotionId, String ageGroup) {
        String key = redisUtil.getAgeGroupCountsKey(promotionId);
        redisUtil.incrementHashField(key, ageGroup, 1);
    }

    // 조회

    public Long getIssuedCount(Long promotionId) {
        String key = redisUtil.getIssuedCountKey(promotionId);
        Object value = redisUtil.getValue(key);
        //TODO: RedisUtil이 반환한 Object를 안전하게 Long으로 변환 (Integer/String 방지 로직 필요)
        return value == null ? 0L : Long.valueOf(value.toString());
    }

    public Long getEstimatedRevenue(Long promotionId) {
        String key = redisUtil.getEstimatedRevenueKey(promotionId);
        Object value = redisUtil.getValue(key);
        return value == null ? 0L : Long.valueOf(value.toString());
    }

    public Map<String, Long> getGenderCounts(Long promotionId) {
        String key = redisUtil.getGenderCountsKey(promotionId);
        return redisUtil.getHashEntries(key);
    }

    public Map<String, Long> getAgeGroupCounts(Long promotionId) {
        String key = redisUtil.getAgeGroupCountsKey(promotionId);
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