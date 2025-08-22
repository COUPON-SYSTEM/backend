package com.company.demo.giftcoupon.outbox.domain.event;

import com.company.demo.common.constant.EventType;

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
        String source,
        LocalDateTime occurredAt
) {
    /** 아웃박스 기록을 위한 래핑(Envelope) */
    public DomainEventEnvelope<CouponIssuedPayload> envelop() {
        return new DomainEventEnvelope<>(
                UUID.randomUUID().toString(),      // event_id
                EventType.ISSUED_EVENT,
                source,
                CouponIssuedPayload.of(memberId, couponId, occurredAt)
        );
    }
}