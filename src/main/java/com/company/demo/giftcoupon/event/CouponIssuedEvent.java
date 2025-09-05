package com.company.demo.giftcoupon.event;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PROTECTED)
public class CouponIssuedEvent {
    private String userId;
    private String message;
}
