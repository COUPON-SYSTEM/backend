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

    // 한 번에 최대 몇 건까지 재전송 시도할지 (튜닝 포인트)
    private static final int MAX_RETRY_PER_RUN = 100;

    @Transactional
    public boolean relayOne() {
        var threshold = LocalDateTime.now().minusMinutes(1);

        var entityOpt = outboxRepository.claimOneUnpublishedForRetry(threshold);
        if (entityOpt.isEmpty()) {
            return false;
        }

        var entity = entityOpt.get();

        try {
            CouponIssuedPayload payload =
                    objectMapper.readValue(entity.getPayload(), CouponIssuedPayload.class);

            DomainEventEnvelope<CouponIssuedPayload> envelope =
                    DomainEventEnvelope.of(
                            entity.getEventId(),
                            entity.getEventType(),
                            entity.getSource(),
                            payload
                    );

            producer.sendIssuedMessage(envelope);
            entity.markPublished(); // 여기까지가 한 건 트랜잭션
        } catch (Exception e) {
            // 실패 → published=false 유지 → 다음에 다시 시도
            log.warn("[failed-relay] publish failed outboxId");
            return true; // 그래도 "작업은 했다"로 보고 true
        }

        return true;
    }

    @Scheduled(fixedDelay = 1000) // 1초 간격 등
    public void relayMany() {
        int count = 0;
        while (count < MAX_RETRY_PER_RUN) {
            boolean processed = relayOne(); // 여기서 매번 새 트랜잭션
            if (!processed) {
                break; // 더 이상 처리할 이벤트 없음
            }
            count++;
        }
        log.info("이번 주기에서 outbox 재시도 {}건 처리", count);
    }
}


