package com.company.demo.giftcoupon.batch;

import com.company.demo.common.constant.EventType;
import com.company.demo.common.response.error.ErrorCode;
import com.company.demo.giftcoupon.batch.exception.SkipDataException;
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
        try {
            Coupon coupon = couponIssueService.issueCoupon(input.getUserId(), input.getPromotionId());

            log.info("사용자 {}에 대한 {}의 이벤트의 쿠폰 생성", coupon.getUserId(), coupon.getPromotionId());

            return ProcessedCouponData.builder()
                    .coupon(coupon)
                    .event(CouponIssuedEvent.of(EventType.ISSUED_EVENT))
                    .build();

        } catch (SkipDataException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            throw new SkipDataException(ErrorCode.BUSINESS_RULE);
        } catch (Exception e) {
            throw e;
        }
    }
}