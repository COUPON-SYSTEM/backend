package com.company.demo.giftcoupon.mapper.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public record CouponIssuanceRequestDto (
    Long memberId,
    Long couponId,
    String requestId,          // 멱등성 키(선택)
    String source,             // 예: "coupon-service"
    LocalDateTime requestedAt
) {
    public CouponIssuanceRequestDto {
            if (memberId == null || couponId == null) throw new IllegalArgumentException("memberId/couponId 필수");
            if (source == null || source.isBlank()) throw new IllegalArgumentException("source 필수");
            if (requestedAt == null) throw new IllegalArgumentException("requestedAt 필수");
    }
}
