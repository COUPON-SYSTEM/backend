package com.company.demo.giftcoupon.outbox.recorder;

import com.company.demo.common.response.error.ErrorCode;
import com.company.demo.giftcoupon.exception.CouponIssueException;
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
    private final CouponIssuanceOutboxRepository couponIssuanceOutboxRepository;
    private final ObjectMapper objectMapper;

    /**
     * Envelope 전체를 JSON으로 저장
     * - eventId: 도메인 이벤트의 eventId와 Envelope의 eventId를 동일하게 맞춘다.
     * - payload only 저장이 아니라 envelope 전체 저장으로 멱등/추적/스키마 진화에 유리.
     */
    public void record(DomainEventEnvelope<CouponIssuedPayload> envelope) {

        String envelopeJson = toJson(envelope);

        CouponIssuanceOutboxEvent row = CouponIssuanceOutboxEvent.builder()
                .eventId(envelope.eventId())
                .eventType(envelope.eventType())
                .payload(envelopeJson)                   // payload를 JSON 저장
                .source(envelope.source())
                .createdAt(LocalDateTime.now())
                .published(false)
                .build();

        couponIssuanceOutboxRepository.save(row);
    }

    private String toJson(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new CouponIssueException(ErrorCode.COUPON_SERIALIZATION_FAILED);
        }
    }
}

