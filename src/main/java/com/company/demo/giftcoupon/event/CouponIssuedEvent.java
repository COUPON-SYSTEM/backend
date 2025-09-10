package com.company.demo.giftcoupon.event;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PROTECTED)
public class CouponIssuedEvent {
    private String couponId;
    private String userId;
    private String message;
}
