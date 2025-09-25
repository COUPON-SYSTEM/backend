package com.company.demo.giftcoupon.event;

import com.company.demo.giftcoupon.outbox.domain.event.CouponIssuedPayload;
import com.company.demo.giftcoupon.outbox.domain.event.DomainEventEnvelope;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record CouponIssueEvent (
    String eventId,
    String memberId,
    String eventType,
    LocalDateTime issuedAt
){
    /** eventId를 내부에서 생성 */
    public DomainEventEnvelope<CouponIssuePayload> toEnvelope(String source) {
        return DomainEventEnvelope.of(
                UUID.randomUUID().toString(),   // eventId
                this.eventType,
                source,
                CouponIssuePayload.of(this.memberId, this.issuedAt)
        );
    }
}
