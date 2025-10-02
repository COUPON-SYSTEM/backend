package com.company.demo.common.config.kafka;

import com.company.demo.giftcoupon.outbox.domain.event.CouponIssuedPayload;
import com.company.demo.giftcoupon.outbox.domain.event.DomainEventEnvelope;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class KafkaConsumerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    private static final String SCHEMA_REGISTRY_URL = "http://localhost:8081";

    // private final KafkaErrorHandler kafkaErrorHandler;

    /**
     * 기본 Consumer 설정을 정의합니다.
     */
    private Map<String, Object> baseConsumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        // JsonDeserializer를 위한 신뢰 패키지 설정 (매우 중요)
        // com.company.demo 아래의 모든 클래스를 신뢰하도록 설정합니다.
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.company.demo.*");
        return props;
    }

    /**
     * CouponIssuedPayload를 역직렬화하는 ConsumerFactory를 생성합니다.
     */
    public ConsumerFactory<String, DomainEventEnvelope<CouponIssuedPayload>> couponIssuedConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                baseConsumerConfigs(),
                new StringDeserializer(),
                new JsonDeserializer<>(DomainEventEnvelope.class, false)
        );
    }

    /**
     * @KafkaListener에서 사용할 리스너 컨테이너 팩토리를 빈으로 등록합니다.
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, DomainEventEnvelope<CouponIssuedPayload>>
    couponIssueKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, DomainEventEnvelope<CouponIssuedPayload>> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(couponIssuedConsumerFactory());
        return factory;
    }
}
