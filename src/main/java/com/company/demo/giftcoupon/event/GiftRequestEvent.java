package com.company.demo.giftcoupon.event;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class GiftRequestEvent {
    private String userId;
}
