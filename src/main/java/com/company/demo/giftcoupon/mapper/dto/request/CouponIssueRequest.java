package com.company.demo.giftcoupon.mapper.dto.request;

import lombok.*;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class CouponIssueRequest {
    private Long userId;
}
