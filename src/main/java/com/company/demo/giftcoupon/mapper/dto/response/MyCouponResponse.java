package com.company.demo.giftcoupon.mapper.dto.response;

import com.company.demo.giftcoupon.domain.entity.Coupon;
import com.company.demo.giftcoupon.domain.entity.CouponMetadata;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MyCouponResponse {
    private Long couponId;
    private String code;
    private Long promotionId;
    private String promotionName;
    private Long value;
    private LocalDateTime expiryDate;
    private boolean used;
    private LocalDateTime usedAt;

    public static MyCouponResponse of(Coupon coupon, CouponMetadata metadata) {
        return MyCouponResponse.builder()
                .couponId(coupon.getId())
                .code(coupon.getCode())
                .promotionId(coupon.getPromotionId())
                .promotionName(metadata != null ? metadata.getName() : null)
                .value(metadata != null ? metadata.getValue() : null)
                .expiryDate(metadata != null ? metadata.getExpiryDate() : null)
                .used(coupon.getUsedAt() != null)
                .usedAt(coupon.getUsedAt())
                .build();
    }
}
