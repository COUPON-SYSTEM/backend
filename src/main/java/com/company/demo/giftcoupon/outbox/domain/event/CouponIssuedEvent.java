package com.company.demo.giftcoupon.outbox.domain.event;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/** 도메인 이벤트: "쿠폰이 발급되었다"
 * record를 사용하는 이유: SpringEvent 상속 방식으로
 * 불변객체를 만들기 위해서는 보일러플레이트가 필요합니다.
 * Spring 4.2 이후부터는 @EventListener가 record 타입을 인식할 수 있습니다.
 */
public record CouponIssuedEvent(
        String eventId,
        Long memberId,
        Long couponId,
        String eventType,
        LocalDateTime issuedAt
) {
    /** eventId를 내부에서 생성 */
    public DomainEventEnvelope<CouponIssuedPayload> toEnvelope(String source) {
        return DomainEventEnvelope.of(
                UUID.randomUUID().toString(),   // eventId
                this.eventType,
                source,
                CouponIssuedPayload.of(this.memberId, this.couponId, this.issuedAt)
        );
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