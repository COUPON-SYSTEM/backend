package com.company.demo.common.client;

import com.company.demo.common.constant.KafkaTopic;
<<<<<<< HEAD:src/main/java/com/company/demo/giftcoupon/consumer/CustomKafkaConsumer.java
import com.company.demo.giftcoupon.outbox.domain.event.CouponIssuedPayload;
import com.company.demo.giftcoupon.outbox.domain.event.DomainEventEnvelope;

=======
import com.company.demo.giftcoupon.outbox.domain.event.DomainEventEnvelope;
import com.company.demo.giftcoupon.event.CouponIssuePayload;
>>>>>>> origin:src/main/java/com/company/demo/common/client/CustomKafkaConsumer.java
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
<<<<<<< HEAD:src/main/java/com/company/demo/giftcoupon/consumer/CustomKafkaConsumer.java
=======
import com.company.demo.giftcoupon.outbox.domain.entity.TryIssueCouponCommand;
import com.company.demo.giftcoupon.sevice.CouponIssueService;
>>>>>>> origin:src/main/java/com/company/demo/common/client/CustomKafkaConsumer.java

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomKafkaConsumer {

<<<<<<< HEAD:src/main/java/com/company/demo/giftcoupon/consumer/CustomKafkaConsumer.java
    @KafkaListener(topics = KafkaTopic.TEST_TOPIC , groupId = "consumer-group1")
=======
    private final CouponIssueService couponIssueService;

    @KafkaListener(topics = KafkaTopic.TEST_TOPIC, groupId = "consumer-group1")
>>>>>>> origin:src/main/java/com/company/demo/common/client/CustomKafkaConsumer.java
    public void handleMessage(String message) {
        log.info("Received message: {}", message);
    }

<<<<<<< HEAD:src/main/java/com/company/demo/giftcoupon/consumer/CustomKafkaConsumer.java
    @KafkaListener(
            topics = KafkaTopic.COUPON_ISSUED,
            containerFactory = "couponIssueKafkaListenerContainerFactory",
            groupId = "consumer-group1"
    )
    public void handleIssuedMessage(DomainEventEnvelope<CouponIssuedPayload> envelope) {
        log.info("Received message: {}", envelope);
=======
    @KafkaListener(topics = KafkaTopic.COUPON_ISSUE, groupId = "consumer-group1")
    public void handleIssuanceMessage(DomainEventEnvelope<CouponIssuePayload> envelope) {
        couponIssueService.tryToIssueCoupon(TryIssueCouponCommand.from(envelope.payload().memberId(), envelope.source()));
>>>>>>> origin:src/main/java/com/company/demo/common/client/CustomKafkaConsumer.java
    }
}