package com.company.demo.giftcoupon.handler.sevice;

import com.company.demo.common.response.error.ErrorCode;
import com.company.demo.common.response.exception.BusinessException;
import com.company.demo.giftcoupon.domain.entity.CouponMetadata;
import com.company.demo.giftcoupon.domain.entity.User;
import com.company.demo.giftcoupon.domain.repository.CouponMetadataRepository;
import com.company.demo.giftcoupon.domain.repository.StatisticsRepository;
import com.company.demo.giftcoupon.mapper.dto.StatisticsDto;
import com.company.demo.giftcoupon.outbox.domain.event.CouponIssuedPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticsService {

    private final UserService userService;
    private final CouponMetadataRepository couponMetadataRepository;
    private final StatisticsRepository statisticsRepository;

    public StatisticsDto processIssuedEvent(CouponIssuedPayload payload) {

        Long couponId = payload.couponId();
        Long userId = payload.userId();

        CouponMetadata metadata = couponMetadataRepository.findByCouponId(couponId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_COUPON_ID));

        Long publisherId = metadata.getPublisherId();
        Long couponValue = metadata.getValue();
        Long totalIssuedCapacity = metadata.getTotalCapacity();

        User user = userService.findById(userId);
        String userGender = user.getGender();
        String userAgeGroup = user.getAgeGroup();

        // Redis 실시간 지표 업데이트

        // 발급 개수 및 총 예상 매출 업데이트
        statisticsRepository.incrementIssuedCount(couponId);
        statisticsRepository.incrementEstimatedRevenue(couponId, couponValue);

        // 이용자 특성별 카운터 업데이트
        statisticsRepository.incrementGenderCount(couponId, userGender);
        statisticsRepository.incrementAgeGroupCount(couponId, userAgeGroup);

        // 최신 통계 데이터 조회 및 DTO 구성

        // 현재 카운트 조회
        Long issuedCount = statisticsRepository.getIssuedCount(couponId);
        Long estimatedRevenue = statisticsRepository.getEstimatedRevenue(couponId);

        // 남은 개수 계산
        Long remainingCount = totalIssuedCapacity - issuedCount;

        // 성별/연령별 분포 비율 계산
        Map<String, Long> totalGenderCounts = statisticsRepository.getGenderCounts(couponId);
        Map<String, Double> genderDistribution = calculateDistribution(totalGenderCounts, issuedCount);

        Map<String, Long> totalAgeCounts = statisticsRepository.getAgeGroupCounts(couponId);
        Map<String, Double> ageDistribution = calculateDistribution(totalAgeCounts, issuedCount);
        Double visitTrendChange = statisticsRepository.getVisitTrendChange(publisherId);

        return new StatisticsDto(
                publisherId,
                couponId,
                issuedCount,
                remainingCount,
                estimatedRevenue,
                visitTrendChange,
                genderDistribution,
                ageDistribution
        );
    }


    private Map<String, Double> calculateDistribution(Map<String, Long> counts, Long total) {
        if (total == null || total == 0) {
            return new ConcurrentHashMap<>();
        }

        Map<String, Double> distribution = new ConcurrentHashMap<>();
        for (Map.Entry<String, Long> entry : counts.entrySet()) {
            double ratio = (double) entry.getValue() / total;
            distribution.put(entry.getKey(), ratio);
        }
        return distribution;
    }
}
