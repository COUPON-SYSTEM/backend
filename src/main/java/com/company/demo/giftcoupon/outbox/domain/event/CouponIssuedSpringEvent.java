package com.company.demo.giftcoupon.outbox.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record CouponIssuedSpringEvent (
        Long eventId,
        Long memberId,
        Long couponId,
        String eventType,
        LocalDateTime issuedAt
) {
    /** eventId를 내부에서 생성 */
    public DomainEventEnvelope<CouponIssuedPayload> toEnvelope(String source) {
        return DomainEventEnvelope.of(
                UUID.randomUUID().toString(),
                this.eventType,
                source,
                CouponIssuedPayload.of(this.memberId, this.couponId, this.issuedAt)
            ;
    }

    /** eventId를 외부에서 주입하고 싶을 때 */
    public DomainEventEnvelope<CouponIssuedPayload> toEnvelope(String source, String eventId) {
        return DomainEventEnvelope.of(
                eventId,
                this.eventType,
                source,
                CouponIssuedPayload.of(this.memberId, this.couponId, this.issuedAt)
        );
    }
}
