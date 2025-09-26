package com.company.demo.common.client;

import com.company.demo.common.constant.KafkaTopic;
import com.company.demo.giftcoupon.event.CouponIssueEvent;
import com.company.demo.giftcoupon.event.CouponIssuePayload;
import com.company.demo.giftcoupon.outbox.domain.event.CouponIssuedPayload;
import com.company.demo.giftcoupon.outbox.domain.event.DomainEventEnvelope;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component // Spring Bean으로 등록
public class CustomKafkaProducer {
    // KafkaTemplate Bean을 주입해 MyKafkaProducer 객체 생성
    private final KafkaTemplate<String, String> kafkaTemplate;
    @Qualifier("giftCouponKafkaTemplate")
    private final KafkaTemplate<String, Object> giftKafkaTemplate;

    @Qualifier("giftCouponKafkaTemplate")
    private final KafkaTemplate<String, Object> issueKafkaTemplate;

    public void sendMessage(String topic, String message) {
        kafkaTemplate.send(topic, message);
        log.info("Message sent to topic " + topic + " : " + message);
    }

    public void sendRequestMessage(DomainEventEnvelope<CouponIssuePayload> envelope) {
        giftKafkaTemplate.send(KafkaTopic.COUPON_ISSUE, envelope);
    }

    public void sendIssuedMessage(DomainEventEnvelope<CouponIssuedPayload> envelope) {
        issueKafkaTemplate.send(KafkaTopic.COUPON_ISSUED, envelope);
    }

/*
    public void sendOrderEvent(OrderEvent event) {
        kafkaTemplate.send("order-topic", event);
    }

    public void sendErrorLog(ErrorEvent event) {
        kafkaTemplate.send("error-log-topic", event);
    }
*/
}
