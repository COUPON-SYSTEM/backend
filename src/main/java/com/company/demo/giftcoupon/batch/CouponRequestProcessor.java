package com.company.demo.giftcoupon.batch;

import com.company.demo.common.constant.EventType;
import com.company.demo.common.constant.Source;
import com.company.demo.giftcoupon.domain.entity.Coupon;
import com.company.demo.giftcoupon.event.CouponIssueEvent;
import com.company.demo.giftcoupon.outbox.domain.event.CouponIssuedEvent;
import com.company.demo.giftcoupon.outbox.domain.event.CouponIssuedPayload;
import com.company.demo.giftcoupon.outbox.domain.event.DomainEventEnvelope;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.batch.item.ItemProcessor;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponRequestProcessor implements ItemProcessor<String, ProcessedCouponData> {

    @Override
    public ProcessedCouponData process(String memberId) {
        // 1) 쿠폰 미리 구성 (DB 저장 금지)
        Coupon coupon = Coupon.builder()
                .memberId(Long.valueOf(memberId))
                .code(Source.COUPON_ISSUED)
                .build();

        // 2) Writer에게 전달할 재료 반환
        return ProcessedCouponData.builder()
                .coupon(coupon)
                .event(CouponIssuedEvent.of(memberId, EventType.ISSUED_EVENT))
                .build();
    }
}
