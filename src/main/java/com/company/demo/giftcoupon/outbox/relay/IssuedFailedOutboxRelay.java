package com.company.demo.giftcoupon.outbox.relay;

import com.company.demo.common.client.CustomKafkaProducer;
import com.company.demo.giftcoupon.outbox.domain.entity.CouponIssuanceOutboxEvent;
import com.company.demo.giftcoupon.outbox.domain.event.CouponIssuedPayload;
import com.company.demo.giftcoupon.outbox.domain.event.DomainEventEnvelope;
import com.company.demo.giftcoupon.outbox.domain.repository.CouponIssuanceOutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Spring Event(After Commit)에서 전송 실패/누락된 것만 회수하는 경량 릴레이.
 * - NEW(방금 생성)는 건드리지 않고,
 * - created_at이 임계시간(threshold)보다 이전인 미발행 레코드만 재시도.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class IssuedFailedOutboxRelay {

    private final CouponIssuanceOutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;
    private final CustomKafkaProducer producer;

    private static final long GRACE_SECONDS = 15;

    @Scheduled(fixedDelayString = "${app.outbox.failed-relay-delay-ms:3000}")
    public void tick() {
        relay();
    }

    @Transactional
    protected void relay() {
        LocalDateTime threshold = LocalDateTime.now().minusSeconds(GRACE_SECONDS);
        CouponIssuanceOutboxEvent e = outboxRepository
                .claimOneUnpublishedForRetry(threshold)
                .orElse(null);

        if (e == null) return;

        try {
            // JSON → 객체 변환
            CouponIssuedPayload payload =
                    objectMapper.readValue(e.getPayload(), CouponIssuedPayload.class);

            // Envelope 생성
            DomainEventEnvelope<CouponIssuedPayload> envelope =
                    DomainEventEnvelope.of(
                            e.getEventId(),
                            e.getEventType(),
                            e.getSource(),
                            payload
                    );

            // Kafka 전송
            producer.sendIssuedMessage(envelope);

            // 발행 완료 마킹
            e.markPublished();
            log.info("[failed-relay] SENT outboxId={}, eventId={}", e.getId(), e.getEventId());

        } catch (Exception ex) {
            log.warn("[failed-relay] publish failed outboxId={}, err={}", e.getId(), ex.toString(), ex);
        }
    }
}
