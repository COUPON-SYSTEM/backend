package com.company.demo.giftcoupon.batch;

import com.company.demo.giftcoupon.domain.entity.Coupon;
import com.company.demo.giftcoupon.outbox.domain.event.CouponIssuedEvent;
import com.company.demo.giftcoupon.outbox.domain.event.CouponIssuedPayload;
import com.company.demo.giftcoupon.outbox.domain.event.DomainEventEnvelope;
import lombok.Builder;


@Builder
public record ProcessedCouponData(
        Coupon coupon,                 // 아직 미저장 엔티티
        CouponIssuedEvent event        // 이벤트
) { }
