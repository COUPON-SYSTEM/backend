package com.company.demo.common.config.kafka;

import com.company.demo.giftcoupon.event.CouponIssuePayload;
import com.company.demo.giftcoupon.outbox.domain.event.CouponIssuedPayload;
import com.company.demo.giftcoupon.outbox.domain.event.DomainEventEnvelope;
import jakarta.annotation.PostConstruct;
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
import com.company.demo.giftcoupon.event.CouponIssueEvent;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String BOOTSTRAP_SERVERS;

    private static final String SCHEMA_REGISTRY_URL = "http://localhost:8081";

    @PostConstruct
    public void checkBootstrapServers() {
        log.info("BOOTSTRAP_SERVERS from config: {}", BOOTSTRAP_SERVERS);
    }

    /** 공통 기본 설정 */
    private Map<String, Object> baseProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 10);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        // 필요 시 배달 타임아웃 등 추가
        // props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 120000);
        return props;
    }


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

    /** String -> DomainEventEnvelope<CouponIssuePayload> 용 */
    @Bean
    public ProducerFactory<String, DomainEventEnvelope<CouponIssuePayload>> couponIssueProducerFactory() {
        Map<String, Object> props = baseProps();
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        // 타입 정보 헤더(기본 true). 소비자에서 필요 없다면 false로.
        // props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, DomainEventEnvelope<CouponIssuePayload>> giftKafkaTemplate() {
        return new KafkaTemplate<>(couponIssueProducerFactory());
    }

    /** String -> DomainEventEnvelope<CouponIssuedPayload> 용 */
    @Bean
    public ProducerFactory<String, DomainEventEnvelope<CouponIssuedPayload>> couponIssuedProducerFactory() {
        Map<String, Object> props = baseProps();
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, DomainEventEnvelope<CouponIssuedPayload>> issueKafkaTemplate() {
        return new KafkaTemplate<>(couponIssuedProducerFactory());
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
