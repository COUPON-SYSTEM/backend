package com.company.demo.giftcoupon.mapper.dto.request;

import lombok.*;

/**
 * 쿠폰 발급 시도 명령
 * - 서비스 메서드 파라미터로 전달
 */
public record TryIssueCouponCommand(
        Long userId
        // String requestId // 멱등성 보장을 위한 요청 ID (optional)
) {
    public static TryIssueCouponCommand from(CouponIssuanceRequestDto requestDto) {
        return new TryIssueCouponCommand(requestDto.getUserId());
    }
}