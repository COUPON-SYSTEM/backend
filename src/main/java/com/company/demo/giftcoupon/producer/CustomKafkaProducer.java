package com.company.demo.giftcoupon.producer;

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
//        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, message);
//        future.whenComplete(((stringStringSendResult, throwable) -> {
//            if(throwable == null) {
//                log.info("sendMessage success, message : {}", message);
//            }
//            else {
//                log.info("sendMessage failed");
//            }
//        }));
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
