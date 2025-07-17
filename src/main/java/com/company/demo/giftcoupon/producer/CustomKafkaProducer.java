package com.company.demo.giftcoupon.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Component // Spring Bean으로 등록
public class CustomKafkaProducer {
    // KafkaTemplate Bean을 주입해 MyKafkaProducer 객체 생성
    private final KafkaTemplate<String, String> kafkaTemplate;

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

}
