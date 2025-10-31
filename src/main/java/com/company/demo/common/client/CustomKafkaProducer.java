package com.company.demo.common.client;

import com.company.demo.common.constant.KafkaTopic;
import com.company.demo.giftcoupon.outbox.domain.event.CouponIssuedPayload;
import com.company.demo.giftcoupon.outbox.domain.event.DomainEventEnvelope;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomKafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaTemplate<String, DomainEventEnvelope<CouponIssuedPayload>> issuedKafkaTemplate;

    public void sendMessage(String topic, String message) {
        kafkaTemplate.send(topic, message);
        log.info("Message sent to topic " + topic + " : " + message);
    }

    public void sendIssuedMessage(DomainEventEnvelope<CouponIssuedPayload> envelope) {
        issuedKafkaTemplate.send(KafkaTopic.COUPON_ISSUED, envelope);
    }
}
