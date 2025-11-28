package com.company.demo.giftcoupon.handler;

import com.company.demo.common.constant.GroupType;
import com.company.demo.common.constant.KafkaTopic;
import com.company.demo.giftcoupon.domain.entity.History;
import com.company.demo.giftcoupon.domain.repository.HistoryRepository;
import com.company.demo.giftcoupon.event.CouponIssuedEvent;
import com.company.demo.giftcoupon.handler.CouponEventHandler;
import com.company.demo.giftcoupon.outbox.domain.event.CouponIssuedPayload;
import com.company.demo.giftcoupon.outbox.domain.event.DomainEventEnvelope;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    @KafkaListener(
            topics = KafkaTopic.COUPON_ISSUED,
            groupId = GroupType.HISTORY,
            containerFactory = "couponIssueKafkaListenerContainerFactory"
    )
    public void handle(DomainEventEnvelope<?> envelope) {
        issuedHistorySave(envelope);
    }

    private void issuedHistorySave(DomainEventEnvelope<?> envelope){
        CouponIssuedPayload payload = objectMapper.convertValue(
                envelope.payload(),
                CouponIssuedPayload.class
        );

        log.info("[쿠폰이력 저장 핸들러]");
        try {
            History history = new History(payload.promotionId(), payload.couponId(), payload.userId(), payload.issuedAt());
            historyRepository.save(history);
            log.info("쿠폰 발급 이력 저장 성공 - CouponId: {}, EventId: {}", payload.couponId(), payload.promotionId());
        } catch (org.springframework.dao.DataIntegrityViolationException dive) {
            log.warn("쿠폰 발급 이력 중복 저장 시도 - 이미 처리된 이벤트일 가능성 높음. EventId: {}", envelope.eventId());
        }catch (Exception e) {
            log.error("쿠폰 발급 이력 저장 실패 - CouponId: {}, EventId: {}", payload.couponId(), payload.promotionId(),
                    e.getMessage(), e);
            throw e;
        }
    }

    @KafkaListener(
            topics = KafkaTopic.COUPON_ISSUED + ".DLT",
            groupId = GroupType.HISTORY_DLQ,
            containerFactory = "dlqListenerContainerFactory"
    )
    public void handleHistoryDLQ(DomainEventEnvelope<?> envelope) {

        if (envelope == null || envelope.payload() == null) {
            log.warn("DLT에서 null 메시지 발견, 스킵");
            return;
        }

        try {
            CouponIssuedPayload payload = objectMapper.convertValue(
                    envelope.payload(),
                    CouponIssuedPayload.class
            );
            log.info("dlq의 payload = {}", payload);
            History history = new History(payload.promotionId(), payload.couponId(), payload.userId(), payload.issuedAt());
            historyRepository.save(history);
            log.info("쿠폰 발급 이력 저장 성공 - CouponId: {}, EventId: {}", payload.couponId(), payload.promotionId());
        } catch (Exception e) {
            log.error("DLT 처리 중 예외 발생 (무시됨): {}", e.getMessage(), e);
        }
    }
}
