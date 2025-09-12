package com.company.demo.giftcoupon.outbox.domain.entity;

import com.company.demo.giftcoupon.outbox.domain.event.DomainEventEnvelope;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "coupon_issuance_outbox_event",
        indexes = {
                @Index(name = "idx_outbox_published_created", columnList = "is_published, created_at")
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CouponIssuanceOutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "outbox_id")
    private Long id;

    @Column(name = "event_id", length = 36, unique = true, nullable = false)
    private String eventId;

    @Column(name = "event_type", length = 100, nullable = false)
    private String eventType;

    @Lob
    @Column(name = "payload", nullable = false)
    private String payload; // JSON

    @Column(name = "source", length = 100, nullable = false)
    private String source;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_published", nullable = false)
    private boolean published;

    public void markPublished() { this.published = true; }


//    public static CouponIssuanceOutboxEvent fromEnvelope(DomainEventEnvelope<?> env, String payloadJson) {
//        return new CouponIssuanceOutboxEvent(
//                env.eventId(),
//                env.eventType(),
//                env.source(),
//                payloadJson,
//                // env.payloadOccurredAt(),  // 없으면 env에서 occurredAt 넘기도록 설계
//                Status.PENDING,
//                LocalDateTime.now()
//        );
//    }
}
