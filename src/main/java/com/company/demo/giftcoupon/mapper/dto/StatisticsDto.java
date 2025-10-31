package com.company.demo.giftcoupon.mapper.dto;

import java.util.Map;

public record StatisticsDto(Long publisherId,       // 쿠폰 발행자 ID
                            Long couponId,          // 특정 쿠폰 식별자
                            Long issuedCount,       // 누적 발급 개수
                            Long remainingCount,    // 남은 개수
                            Long estimatedRevenue,  // 총 예상 매출
                            Double visitTrendChange,// 방문 추이 (예: 이전 1시간 대비 % 변화, 이 부분은 별도 스트림 처리 권장)
                            Map<String, Double> genderDistribution, // 성별 비율 (예: {"MALE": 0.6, "FEMALE": 0.4})
                            Map<String, Double> ageDistribution) {
}
