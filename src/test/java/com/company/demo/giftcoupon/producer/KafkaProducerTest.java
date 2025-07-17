package com.company.demo.giftcoupon.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class KafkaProducerTest {

    private KafkaTemplate<String, String> kafkaTemplate;
    private CustomKafkaProducer kafkaProducer;

    @BeforeEach
    void setUp() {
        kafkaTemplate = Mockito.mock(KafkaTemplate.class); // Template을 모방
        kafkaProducer = new CustomKafkaProducer(kafkaTemplate);
    }

    @Test
    @DisplayName("테스트 토픽으로 메시지 보내기")
    void testSendMessage() {
        // Given
        String topic = "test-topic";
        String message = "Hello, Kafka!";

        // When
        kafkaProducer.sendMessage(topic, message);

        // Then
        verify(kafkaTemplate, times(1)).send(topic, message);
    }
}