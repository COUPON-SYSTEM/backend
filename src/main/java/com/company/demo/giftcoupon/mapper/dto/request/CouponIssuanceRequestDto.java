package com.company.demo.giftcoupon.mapper.dto.request;

import lombok.*;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class CouponIssuanceRequestDto {
    private Long memberId;
    private String source;
}
