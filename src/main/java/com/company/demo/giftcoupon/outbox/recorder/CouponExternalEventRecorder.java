package com.company.demo.giftcoupon.outbox.recorder;

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

    /**
     * Envelope 전체를 JSON으로 저장 (권장)
     * - eventId: 도메인 이벤트의 eventId와 Envelope의 eventId를 동일하게 맞춘다.
     * - payload only 저장이 아니라 envelope 전체 저장으로 멱등/추적/스키마 진화에 유리.
     */
    public void record(DomainEventEnvelope<CouponIssuedPayload> envelope) {

        String envelopeJson = toJson(envelope);

        CouponIssuanceOutboxEvent row = CouponIssuanceOutboxEvent.builder()
                .eventId(envelope.eventId())             // = event.eventId()
                .eventType(envelope.eventType())         // "coupon.issued" 등
                .payload(envelopeJson)                   // ★ Envelope 전체 JSON 저장
                .source(envelope.source())               // "giftcoupon-service"
                .createdAt(LocalDateTime.now())
                .published(false)
                .build();

        repository.save(row);
    }

    private String toJson(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Outbox 직렬화 실패", e);
        }
    }
}

