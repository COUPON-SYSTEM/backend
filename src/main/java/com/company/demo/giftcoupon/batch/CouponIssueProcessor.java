package com.company.demo.giftcoupon.batch;

import com.company.demo.common.constant.EventType;
import com.company.demo.giftcoupon.domain.entity.Coupon;
import com.company.demo.giftcoupon.outbox.domain.event.CouponIssuedEvent;
import com.company.demo.giftcoupon.sevice.CouponIssueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.batch.item.ItemProcessor;


@Slf4j
@Component
@RequiredArgsConstructor
public class CouponIssueProcessor implements ItemProcessor<String, ProcessedCouponData> {

    private final CouponIssueService couponIssueService;

    @Override
    public ProcessedCouponData process(String userId) {
        // 1) 쿠폰 미리 구성 (DB 저장 금지)
        Coupon coupon = couponIssueService.issueCoupon(userId);
        log.info("사용자 {}에 대한 쿠폰 생성", coupon.getUserId());

        // 2) Writer에게 전달할 재료 반환
        return ProcessedCouponData.builder()
                .coupon(coupon)
                .event(CouponIssuedEvent.of(coupon.getUserId(), EventType.ISSUED_EVENT))
                .build();
    }
}
