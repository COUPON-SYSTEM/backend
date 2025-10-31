package com.company.demo.giftcoupon.handler;

import com.company.demo.common.constant.GroupType;
import com.company.demo.common.constant.KafkaTopic;
import com.company.demo.giftcoupon.event.CouponIssuedEvent;
import com.company.demo.giftcoupon.handler.CouponEventHandler;
import com.company.demo.giftcoupon.outbox.domain.event.CouponIssuedPayload;
import com.company.demo.giftcoupon.outbox.domain.event.DomainEventEnvelope;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.amqp.core.AmqpTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExternalListener implements CouponEventHandler { // 외부시스템에 데이터를 동기화

    private final AmqpTemplate amqpTemplate;

    @Override
    @KafkaListener(
            topics = KafkaTopic.COUPON_ISSUED,
            groupId = GroupType.EXTERNAL,
            containerFactory = "couponIssueKafkaListenerContainerFactory"
    )
    public void handle(DomainEventEnvelope<CouponIssuedPayload> envelope) {
        externalNotification(envelope);
    }

    private void externalNotification(DomainEventEnvelope<CouponIssuedPayload> envelope){
        CouponIssuedPayload payload = envelope.payload();
        try {
            // 메시지 큐로 이벤트 데이터 발행
            // 'exchange'는 메시지를 라우팅하는 교환소, 'routing-key'는 라우팅 키
            // amqpTemplate.convertAndSend("external-system-exchange", "coupon.issued", payload);

            log.info("메시지 큐에 이벤트 발행 완료 - CouponId: {}", payload.couponId());
        } catch (Exception e) {
            log.error("메시지 큐 발행 실패 - CouponId: {}", payload.couponId(), e.getMessage(), e);
            throw e;
        }
    }
}
