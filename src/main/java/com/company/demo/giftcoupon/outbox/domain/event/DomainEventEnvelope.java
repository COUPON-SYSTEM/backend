package com.company.demo.giftcoupon.outbox.domain.event;

/** Envelope: Outbox 테이블의 event_id / type / source / payload에 매핑 */
public record DomainEventEnvelope<T>(
        String eventId,
        String eventType,
        String source,
        T payload
) {}
