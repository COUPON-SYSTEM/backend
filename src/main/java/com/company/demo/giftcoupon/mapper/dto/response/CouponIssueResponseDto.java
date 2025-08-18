package com.company.demo.giftcoupon.mapper.dto.response;

import com.company.demo.giftcoupon.domain.entity.Coupon;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CouponIssueResponseDto {
    private String code;

    public static CouponIssueResponseDto of(Coupon coupon) {
        return CouponIssueResponseDto.builder()
                        .code(coupon.getCode())
                                .build();
    }
}
