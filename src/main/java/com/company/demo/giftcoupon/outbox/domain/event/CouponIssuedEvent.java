package com.company.demo.giftcoupon.outbox.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

/** 도메인 이벤트: "쿠폰이 발급되었다"
 * record를 사용하는 이유: SpringEvent 상속 방식으로
 * 불변객체를 만들기 위해서는 보일러플레이트가 필요합니다.
 * Spring 4.2 이후부터는 @EventListener가 record 타입을 인식할 수 있습니다.
 */

public record CouponIssuedEvent(
        Long memberId,
        Long couponId,
        String eventType,
        LocalDateTime occurredAt
) {
    /** eventId를 내부에서 생성 */
    public DomainEventEnvelope<CouponIssuedPayload> toEnvelope(String source) {
        return DomainEventEnvelope.of(
                UUID.randomUUID().toString(),
                eventType,
                source,
                CouponIssuedPayload.of(memberId, couponId, occurredAt)
        );
    }

    /** eventId를 외부에서 주입하고 싶을 때 */
    public DomainEventEnvelope<CouponIssuedPayload> toEnvelope(String source, String eventId) {
        return DomainEventEnvelope.of(
                eventId,
                eventType,
                source,
                CouponIssuedPayload.of(memberId, couponId, occurredAt)
        );
    }
}