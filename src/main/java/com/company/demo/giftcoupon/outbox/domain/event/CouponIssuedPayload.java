package com.company.demo.giftcoupon.outbox.domain.event;

import lombok.Builder;

import java.time.LocalDateTime;

public record CouponIssuedPayload(
        Long memberId,
        Long couponId,
        LocalDateTime issuedAt
) {
    @Builder
    public static CouponIssuedPayload of(Long memberId, Long couponId, LocalDateTime issuedAt) {
        return CouponIssuedPayload.builder()
                .memberId(memberId)
                .couponId(couponId)
                .issuedAt(issuedAt)
                .build();
    }
}