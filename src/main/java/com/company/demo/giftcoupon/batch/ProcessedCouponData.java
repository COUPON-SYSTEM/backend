package com.company.demo.giftcoupon.batch;

import com.company.demo.giftcoupon.domain.entity.Coupon;
import lombok.Builder;


@Builder
public record ProcessedCouponData(
        Coupon coupon,                                            // 아직 미저장 엔티티
        DomainEventEnvelope<CouponIssuedPayload> envelope          // Outbox에 기록할 이벤트 봉투
) { }
