package com.company.demo.common.client;

import com.company.demo.common.constant.KafkaTopic;
import com.company.demo.giftcoupon.event.CouponRequestEvent;
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
    private final KafkaTemplate<String, CouponRequestEvent> giftKafkaTemplate;

    public void sendMessage(String topic, String message) {
        kafkaTemplate.send(topic, message);
        log.info("Message sent to topic " + topic + " : " + message);
    }

    public void sendRequestMessage(CouponRequestEvent event) {
        giftKafkaTemplate.send(KafkaTopic.COUPON_REQUEST, event);
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
