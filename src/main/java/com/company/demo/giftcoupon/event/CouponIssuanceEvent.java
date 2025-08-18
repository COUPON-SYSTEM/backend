package com.company.demo.giftcoupon.event;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class CouponIssuanceEvent {
    private String userId;
}
