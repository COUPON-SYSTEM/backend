package com.company.demo.giftcoupon.outbox.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

/** 도메인 이벤트: "쿠폰이 발급되었다" */
public record CouponIssuedEvent(
        Long memberId,
        Long couponId,
        String source,
        LocalDateTime occurredAt
) {
    public String eventType() {
        return "CouponIssuedEvent";
    }

    /** 아웃박스 기록을 위한 래핑(Envelope) */
    public DomainEventEnvelope<CouponIssuedPayload> envelop() {
        return new DomainEventEnvelope<>(
                UUID.randomUUID().toString(),      // event_id
                eventType(),
                source,
                new CouponIssuedPayload(memberId, couponId, occurredAt)
        );
    }
}