package com.company.demo.giftcoupon.outbox.relay;

import com.company.demo.giftcoupon.outbox.domain.entity.CouponIssuanceOutboxEvent;
import com.company.demo.giftcoupon.outbox.domain.event.DomainEventEnvelope;
import com.company.demo.giftcoupon.outbox.domain.repository.CouponIssuanceOutboxRepository;
import com.company.demo.giftcoupon.outbox.publisher.KafkaOutboxPublisher;
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
    private final KafkaOutboxPublisher publisher;

    // 리스너가 처리할 시간을 주기 위한 그레이스(예: 15초)
    private static final long GRACE_SECONDS = 15;

    @Scheduled(fixedDelayString = "${app.outbox.failed-relay-delay-ms:3000}")
    public void tick() {
        relayOnce();
    }

    @Transactional
    protected void relayOnce() {
        // (1) 리스너가 처리하지 못하고 남아있을 법한, 충분히 오래된 미발행 1건 선점
        LocalDateTime threshold = LocalDateTime.now().minusSeconds(GRACE_SECONDS);
        CouponIssuanceOutboxEvent e = outboxRepository
                .claimOneUnpublishedForRetry(threshold)
                .orElse(null);

        if (e == null) return;

        try {
            // (2) Envelope로 변환
            DomainEventEnvelope<String> envelope =
                    DomainEventEnvelope.of(
                            e.getEventId(),
                            e.getEventType(),
                            e.getSource(),
                            e.getPayload() // JSON 문자열(퍼블리셔에서 필요시 POJO로 변환)
                    );

            // (3) 외부 전송(동기 확인)
            publisher.publish(envelope);

            // (4) 성공 → is_published = true
            e.markPublished(); // JPA 엔티티 변경 감지로 update
            log.info("[failed-relay] SENT outboxId={}, eventId={}", e.getId(), e.getEventId());

        } catch (Exception ex) {
            // 실패 시엔 그대로 두면 됨(다음 tick에 다시 시도)
            // 필요하다면 로그/알림 추가
            log.warn("[failed-relay] publish failed outboxId={}, err={}", e.getId(), ex.toString(), ex);
            // ※ attempts/backoff 컬럼이 없으니, 임계치/알람은 모니터링으로 처리
        }
    }
}
