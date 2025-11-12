package com.company.demo.giftcoupon.outbox.publisher;

import com.company.demo.common.client.CustomKafkaProducer;
import com.company.demo.giftcoupon.outbox.domain.event.CouponIssuedPayload;
import com.company.demo.giftcoupon.outbox.domain.event.DomainEventEnvelope;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaOutboxPublisher {

    private final CustomKafkaProducer producer;

    public void publish(DomainEventEnvelope<CouponIssuedPayload> envelope) {
        producer.sendIssuedMessage(envelope);
    }
}