package com.company.demo.giftcoupon.outbox.domain.event;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
public record CouponIssuedPayload(
        Long userId,
        Long couponId,
        LocalDateTime issuedAt,
        Long promotionId
) {
    public static CouponIssuedPayload of(Long userId, Long couponId, LocalDateTime issuedAt, Long promotionId) {
        return CouponIssuedPayload.builder()
                .userId(userId)
                .couponId(couponId)
                .issuedAt(issuedAt)
                .promotionId(promotionId)
                .build();
    }
}