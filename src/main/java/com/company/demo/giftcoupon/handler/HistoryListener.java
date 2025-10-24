package com.company.demo.giftcoupon.handler;

import com.company.demo.common.constant.GroupType;
import com.company.demo.common.constant.KafkaTopic;
import com.company.demo.giftcoupon.domain.entity.History;
import com.company.demo.giftcoupon.domain.repository.HistoryRepository;
import com.company.demo.giftcoupon.event.CouponIssuedEvent;
import com.company.demo.giftcoupon.handler.CouponEventHandler;
import com.company.demo.giftcoupon.outbox.domain.event.CouponIssuedPayload;
import com.company.demo.giftcoupon.outbox.domain.event.DomainEventEnvelope;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@Slf4j
@RequiredArgsConstructor
public class HistoryListener implements CouponEventHandler {

    private final HistoryRepository historyRepository;

    @Override
    @Transactional
    @KafkaListener(
            topics = KafkaTopic.COUPON_ISSUED,
            groupId = GroupType.HISTORY,
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handle(DomainEventEnvelope<CouponIssuedPayload> envelope) {
        issuedHistorySave(envelope);
    }

    private void issuedHistorySave(DomainEventEnvelope<CouponIssuedPayload> envelope){
        CouponIssuedPayload payload = envelope.payload();
        try {
            History history = new History(payload.couponId(), payload.userId(), payload.issuedAt());
            historyRepository.save(history);
            log.info("쿠폰 발급 이력 저장 성공 - CouponId: {}", payload.couponId());
        } catch (Exception e) {
            log.error("쿠폰 발급 이력 저장 실패 - CouponId: {}", payload.couponId(), e.getMessage(), e);
            throw e;
        }
    }

    // DLQ 처리 (각 서비스별 독립적)
    @KafkaListener(
            topics = KafkaTopic.COUPON_ISSUED + ".DLT",
            groupId = "history-service-dlq",
            containerFactory = "dlqListenerContainerFactory" // DLQ 전용 팩토리
    )
    public void handleHistoryDLQ(DomainEventEnvelope<CouponIssuedPayload> envelope) {
        log.error("쿠폰 이력 저장 최종 실패 - CouponId: {}", envelope.payload().couponId());
    }
}
