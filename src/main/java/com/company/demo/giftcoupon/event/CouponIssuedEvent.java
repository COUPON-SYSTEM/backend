package com.company.demo.giftcoupon.event;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PROTECTED)
public class CouponIssuedEvent {
    private Long couponId;
    private Long userId;
    private String message;
}
