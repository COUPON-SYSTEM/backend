package com.company.demo.common.client;

import com.company.demo.common.constant.KafkaTopic;
import com.company.demo.giftcoupon.outbox.domain.event.CouponIssuedPayload;
import com.company.demo.giftcoupon.outbox.domain.event.DomainEventEnvelope;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomKafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaTemplate<String, DomainEventEnvelope<CouponIssuedPayload>> issuedKafkaTemplate;

    @Qualifier("giftCouponKafkaTemplate")
    private final KafkaTemplate<String, Object> giftKafkaTemplate;

    @Qualifier("giftCouponKafkaTemplate")
    private final KafkaTemplate<String, Object> issueKafkaTemplate;

    public void sendMessage(String topic, String message) {
        kafkaTemplate.send(topic, message);
        log.info("Message sent to topic " + topic + " : " + message);
    }

    public void sendIssuedMessage(DomainEventEnvelope<CouponIssuedPayload> envelope) throws ExecutionException, InterruptedException {
        issuedKafkaTemplate.send(KafkaTopic.COUPON_ISSUED, envelope)
                .get();
    }
}
