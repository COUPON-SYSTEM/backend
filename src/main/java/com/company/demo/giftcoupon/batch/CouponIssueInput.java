package com.company.demo.giftcoupon.batch;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Data
public class CouponIssueInput {
    private String userId;
    private String promotionId;
}
