package com.company.demo.giftcoupon.event;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class CouponRequestEvent {
    private String userId;
}
