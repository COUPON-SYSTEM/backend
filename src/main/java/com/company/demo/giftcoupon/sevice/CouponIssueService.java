package com.company.demo.giftcoupon.sevice;

import com.company.demo.common.constant.EventType;
import com.company.demo.giftcoupon.domain.entity.Coupon;
import com.company.demo.giftcoupon.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class CouponIssueService {
    public Coupon issueCoupon(final String userId, final String couponId) {
        return Coupon.builder()
                .userId(Long.valueOf(userId))
                .promotionId(Long.valueOf(couponId))
                .code("안녕하세요")
                .eventType(EventType.ISSUED_EVENT)
                .build();
    }
}