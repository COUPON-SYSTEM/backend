package com.company.demo.giftcoupon.event;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CouponIssuePayload(
        String memberId,
        LocalDateTime issuedAt
) {
    public static CouponIssuePayload of(String memberId, LocalDateTime issuedAt) {
        return CouponIssuePayload.builder()
                .memberId(memberId)
                .issuedAt(issuedAt)
                .build();
    }
}

