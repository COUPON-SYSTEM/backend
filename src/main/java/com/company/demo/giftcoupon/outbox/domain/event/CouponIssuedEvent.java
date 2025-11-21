package com.company.demo.giftcoupon.outbox.domain.event;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

/** 도메인 이벤트: "쿠폰이 발급되었다"
 * record를 사용하는 이유: SpringEvent 상속 방식으로
 * 불변객체를 만들기 위해서는 보일러플레이트가 필요합니다.
 * Spring 4.2 이후부터는 @EventListener가 record 타입을 인식할 수 있습니다.
 */
@Builder
public record CouponIssuedEvent(
        String eventId,
        String eventType,
        LocalDateTime issuedAt
) {
    public static CouponIssuedEvent of(String eventType) {
        return CouponIssuedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(eventType)
                .issuedAt(LocalDateTime.now())
                .build();
    }

    /** Envelope 변환 */
    public DomainEventEnvelope<CouponIssuedPayload> toEnvelope(String source, Long couponId, Long promotionId, Long userId) {
        return DomainEventEnvelope.of(
                this.eventId,
                this.eventType,
                source,
                CouponIssuedPayload.of(userId, couponId, this.issuedAt, promotionId)
        );
    }
}