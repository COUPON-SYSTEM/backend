package com.company.demo.giftcoupon.outbox;

import com.company.demo.giftcoupon.outbox.domain.event.CouponIssuedPayload;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.company.demo.giftcoupon.outbox.domain.entity.CouponIssuanceOutboxEvent;
import com.company.demo.giftcoupon.outbox.domain.repository.CouponIssuanceOutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.company.demo.giftcoupon.outbox.domain.event.DomainEventEnvelope;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CouponExternalEventRecorder {
    private final CouponIssuanceOutboxRepository repository;
    private final ObjectMapper objectMapper;

    /** Envelope.payload를 JSON으로 저장 */
    public void record(DomainEventEnvelope<CouponIssuedPayload> env) {
        String json = toJson(env.payload());
        CouponIssuanceOutboxEvent row = CouponIssuanceOutboxEvent.builder()
                .eventId(env.eventId())
                .eventType(env.eventType())
                .payload(json)
                .source(env.source())
                .createdAt(LocalDateTime.now())
                .published(false)
                .build();
        repository.save(row);
    }

    private String toJson(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Outbox payload 직렬화 실패", e);
        }
    }
}
