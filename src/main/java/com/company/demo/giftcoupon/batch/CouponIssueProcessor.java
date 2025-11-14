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
public class CouponIssueProcessor implements ItemProcessor<CouponIssueInput, ProcessedCouponData> {

    private final CouponIssueService couponIssueService;

    @Override
    public ProcessedCouponData process(CouponIssueInput input) {
        String userId = input.getUserId();
        String promotionId = input.getPromotionId();

        // 1) 쿠폰 미리 구성 (DB 저장 금지)
        Coupon coupon = couponIssueService.issueCoupon(userId, promotionId);
        log.info("사용자 {}에 대한 {}의 이벤트의 쿠폰 생성", coupon.getUserId(), coupon.getPromotionId());

        // 2) Writer에게 전달할 재료 반환
        return ProcessedCouponData.builder()
                .coupon(coupon)
                .event(CouponIssuedEvent.of(EventType.ISSUED_EVENT))
                .build();
    }
}
