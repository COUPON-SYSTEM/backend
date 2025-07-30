package com.company.demo.common.config.kafka;

import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.core.ProducerFactory;
import com.company.demo.giftcoupon.event.CouponRequestEvent;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String BOOTSTRAP_SERVERS;

    private static final String SCHEMA_REGISTRY_URL = "http://localhost:8081";

    // 동적 생성을 가정하여 설정
    // Kafka 프로듀서 인스턴스(KafkaTemplate)를 생성하는 팩토리 객체
    private <T> ProducerFactory<String, T> producerFactory(Class<T> clazz) {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
//        props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
//        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        props.put(ProducerConfig.ACKS_CONFIG, "all");  // 안정성 향상 (메세지 전송 확인 수준 설정)
        props.put(ProducerConfig.RETRIES_CONFIG, 10);  // 실패 시 재시도 횟수 설정
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1); //  "얼마나 기다렸다가" 배치로 전송할지를 결정하는 지연 시간(ms 단위)

        return new DefaultKafkaProducerFactory<>(props);
    }

    // 실제로 Kafka에 메시지를 전송하는 데 사용하는 스프링 추상화 객체: KafkaTemplates
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory(String.class));
    }

    @Bean
    public KafkaTemplate<String, CouponRequestEvent> giftKafkaTemplate() {
        return new KafkaTemplate<>(producerFactory(CouponRequestEvent.class));
    }

//    @Bean
//    public KafkaTemplate<String, CouponIssuedEvent> couponKafkaTemplate() {
//        return new KafkaTemplate<>(producerFactory(CouponIssuedEvent.class));
//    }
//
//    @Bean
//    public KafkaTemplate<String, NotificationEvent> notificationKafkaTemplate() {
//        return new KafkaTemplate<>(producerFactory(NotificationEvent.class));
//    }
}
