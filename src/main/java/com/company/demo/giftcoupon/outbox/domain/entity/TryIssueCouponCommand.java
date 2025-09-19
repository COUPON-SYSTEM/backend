package com.company.demo.giftcoupon.outbox.domain.entity;

import com.company.demo.common.constant.Source;
import com.company.demo.giftcoupon.event.CouponIssueEvent;
import com.company.demo.giftcoupon.mapper.dto.request.CouponIssueRequest;

/**
 * 쿠폰 발급 시도 명령
 * - 서비스 메서드 파라미터로 전달
 */
public record TryIssueCouponCommand(
        Long memberId,
        String source
        // String requestId // 멱등성 보장을 위한 요청 ID (optional)
) {
    public static TryIssueCouponCommand from(String memberId, String source) {
        return new TryIssueCouponCommand(Long.parseLong(memberId), source);
    }
}