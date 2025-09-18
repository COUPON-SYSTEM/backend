package com.company.demo.giftcoupon.producer;

import com.company.demo.common.constant.KafkaTopic;
import com.company.demo.giftcoupon.event.CouponIssuanceEvent;
import com.company.demo.giftcoupon.outbox.domain.event.CouponIssuedPayload;
import com.company.demo.giftcoupon.outbox.domain.event.DomainEventEnvelope;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component // Spring Bean으로 등록
public class CustomKafkaProducer {
    // KafkaTemplate Bean을 주입해 MyKafkaProducer 객체 생성
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaTemplate<String, CouponIssuanceEvent> giftKafkaTemplate;
    private final KafkaTemplate<String, CouponIssuedPayload> issueKafkaTemplate;

    public void sendMessage(String topic, String message) {
        kafkaTemplate.send(topic, message);
        log.info("Message sent to topic " + topic + " : " + message);
    }

    public void sendRequestMessage(CouponIssuanceEvent event) {
        giftKafkaTemplate.send(KafkaTopic.COUPON_ISSUANCE, event);
    }

    public void sendIssuedMessage(DomainEventEnvelope<CouponIssuedPayload> env ) {
        CouponIssuedPayload payload = env.payload(); // 여기서 봉투를 푼다.
        // 이 payload를 JSON 등으로 직렬화하여 KafkaTemplate.send()에 전달
        issueKafkaTemplate.send(KafkaTopic.COUPON_ISSUED, payload);
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
