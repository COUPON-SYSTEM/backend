package com.company.demo.giftcoupon.event;

import lombok.Builder;

import java.time.LocalDateTime;

public record CouponIssuePayload(
        String memberId,
        LocalDateTime issuedAt
) {
    @Builder
    public static CouponIssuePayload of(String memberId, LocalDateTime issuedAt) {
        return new CouponIssuePayload(memberId, issuedAt);
    }
}

