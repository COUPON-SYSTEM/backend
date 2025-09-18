package com.company.demo.giftcoupon.mapper.dto.response;

import com.company.demo.giftcoupon.domain.entity.Coupon;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CouponIssueResponse {
    private String code;

    public static CouponIssueResponse of(Coupon coupon) {
        return CouponIssueResponse.builder()
                        .code(coupon.getCode())
                                .build();
    }
}
