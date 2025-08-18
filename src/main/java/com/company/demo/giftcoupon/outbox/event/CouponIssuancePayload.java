package com.company.demo.giftcoupon.outbox.event;

import java.time.LocalDateTime;

public record CouponIssuancePayload(
        Long memberId,
        Long couponId,
        LocalDateTime issuedAt
) {}