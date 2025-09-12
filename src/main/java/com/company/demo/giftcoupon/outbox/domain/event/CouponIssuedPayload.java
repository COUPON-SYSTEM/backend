package com.company.demo.giftcoupon.outbox.domain.event;

import lombok.Builder;
import lombok.Getter;

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
    @Builder
    public static CouponIssuedPayload from(DomainEventEnvelope<?> env) {
        // 안전하게 캐스팅
        if (!(env.payload() instanceof CouponIssuedPayload payload)) {
            throw new IllegalArgumentException("Invalid payload type: " + env.payload().getClass());
        }

        return CouponIssuedPayload.builder()
                .memberId(payload.memberId())
                .couponId(payload.couponId())
                .issuedAt(payload.issuedAt())
                .build();
    }
}