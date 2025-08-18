package com.company.demo.giftcoupon.outbox.domain.event;

import java.time.LocalDateTime;

public record CouponIssuedPayload(
        Long memberId,
        Long couponId,
        LocalDateTime issuedAt
) {}