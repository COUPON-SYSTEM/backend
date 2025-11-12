package com.company.demo.giftcoupon.outbox.relay;

import com.company.demo.giftcoupon.outbox.domain.entity.CouponIssuanceOutboxEvent;
import com.company.demo.giftcoupon.outbox.domain.repository.CouponIssuanceOutboxRepository;
import com.company.demo.giftcoupon.outbox.publisher.KafkaOutboxPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class IssuedFailedOutboxRelay {
    private final CouponIssuanceOutboxRepository outboxRepository;
    private final KafkaOutboxPublisher publisher;

    private static final Duration LOCK_TTL = Duration.ofSeconds(30);

    // "실패만 재시도"니까 주기는 넉넉히. 필요하면 2~5초로.
    @Scheduled(fixedDelayString = "${app.outbox.failed-relay-delay-ms:3000}")
    public void tick() {
        var o = claimOneFailed();
        if (o == null) return;

        try {
            publisher.publish(o.getEventId(), o.getEventType(), o.getPayload());
            markSent(o.getId());
            log.info("[failed-relay] SENT id={}, eventId={}", o.getId(), o.getEventId());
        } catch (Exception e) {
            handleBackoff(o.getId(), o.getAttempts(), e);
            log.warn("[failed-relay] retry failed id={}, attempts={}, err={}", o.getId(), o.getAttempts(), e.toString());
        }
    }

    @Transactional
    protected CouponIssuanceOutboxEvent claimOneFailed() {
        return outboxRepository.claimOneFailedForRetry(LocalDateTime.now(), LOCK_TTL).orElse(null);
    }

    @Transactional
    protected void markSent(Long id) {
        outboxRepository.markSent(id);
    }

    @Transactional
    protected void handleBackoff(Long id, int attemptsSoFar, Exception e) {
        int nextSec = (int) Math.pow(2, Math.min(attemptsSoFar + 1, 8)); // 1~256초
        LocalDateTime nextRetryAt = LocalDateTime.now().plusSeconds(nextSec);
        String err = truncate(e.toString(), 4000);
        outboxRepository.backoff(id, nextRetryAt, err);
    }

    private String truncate(String s, int max) {
        return (s == null || s.length() <= max) ? s : s.substring(0, max);
    }
}
