package com.company.demo.giftcoupon.consumer;

import com.company.demo.common.constant.KafkaTopic;
import com.company.demo.giftcoupon.outbox.domain.event.CouponIssuedPayload;
import com.company.demo.giftcoupon.outbox.domain.event.DomainEventEnvelope;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomKafkaConsumer {

    @KafkaListener(topics = KafkaTopic.TEST_TOPIC , groupId = "consumer-group1")
    public void handleMessage(String message) {
        log.info("Received message: {}", message);
    }

    @KafkaListener(
            topics = KafkaTopic.COUPON_ISSUED,
            containerFactory = "couponIssueKafkaListenerContainerFactory",
            groupId = "consumer-group1"
    )
    public void handleIssuedMessage(DomainEventEnvelope<CouponIssuedPayload> envelope) {
        log.info("Received message: {}", envelope);
    }
}
