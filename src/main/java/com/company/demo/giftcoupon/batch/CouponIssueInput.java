package com.company.demo.giftcoupon.batch;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CouponIssueInput {
    private String userId;
    private Long promotionId;
}
