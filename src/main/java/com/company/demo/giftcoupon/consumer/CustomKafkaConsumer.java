package com.company.demo.giftcoupon.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomKafkaConsumer {
    @KafkaListener(topics = "test-topic", groupId = "consumer-group1")
    public void handleMessage(String message) {
        log.info("Received message: {}", message);
    }
}
