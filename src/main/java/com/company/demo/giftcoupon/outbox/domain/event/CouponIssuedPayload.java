package com.company.demo.giftcoupon.outbox.domain.event;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
public record CouponIssuedPayload(
        Long memberId,
        Long couponId,
        LocalDateTime issuedAt
) {
    public static CouponIssuedPayload of(Long memberId, Long couponId, LocalDateTime issuedAt) {
        return CouponIssuedPayload.builder()
                .memberId(memberId)
                .couponId(couponId)
                .issuedAt(issuedAt)
                .build();
    }
}