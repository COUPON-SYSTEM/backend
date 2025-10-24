package com.company.demo.common.config.kafka;

<<<<<<< HEAD
import com.company.demo.giftcoupon.outbox.domain.event.CouponIssuedPayload;
import com.company.demo.giftcoupon.outbox.domain.event.DomainEventEnvelope;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
=======
import com.company.demo.giftcoupon.event.CouponIssueEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
>>>>>>> origin
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
<<<<<<< HEAD
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
=======
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;
>>>>>>> origin

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId; // TODO: 모든 컨슈머가 같은 group-id를 사용하면 안됨

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String SCHEMA_REGISTRY_URL = "http://localhost:8081";

    // private final KafkaErrorHandler kafkaErrorHandler;

<<<<<<< HEAD
    /**
     * 기본 Consumer 설정을 정의합니다.
     */
    private Map<String, Object> baseConsumerConfigs() {
=======
    public <T> ConsumerFactory<String, T> consumerFactory(Class<T> clazz) {
        JsonDeserializer<T> deserializer = new JsonDeserializer<>(clazz);
        deserializer.addTrustedPackages("*"); //TODO: 신뢰할 패키지 제한

>>>>>>> origin
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
<<<<<<< HEAD
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
=======
        // props.put(ConsumerConfig.ALLOW_AUTO_CREATE_TOPICS_CONFIG, false); - 자동 토픽 생성 방지

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    public ConsumerFactory<String, Object> consumerFactory() {
        JsonDeserializer<Object> deserializer = new JsonDeserializer<>(Object.class);
        deserializer.addTrustedPackages("*"); //TODO: 신뢰할 패키지 제한

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
>>>>>>> origin

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
<<<<<<< HEAD
=======

    // DLQ를 위한 Container Factory
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(3); // 병렬 처리 스레드 수
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);

        // DLQ 설정
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (record, exception) -> {
                    // DLT 토픽 이름 규칙: 원본토픽.DLT
                    String dltTopicName = record.topic() + ".DLT";
                    log.warn("메시지를 DLT로 전송 - 원본토픽: {}, DLT토픽: {}, 오류: {}",
                            record.topic(), dltTopicName, exception.getMessage());
                    return new TopicPartition(dltTopicName, 0); // 파티션 0으로 통일
                }
        );

        // 재시도 정책: 1초 간격으로 3번 재시도
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
                recoverer,
                new FixedBackOff(1000L, 3)
        );

        // 특정 예외는 재시도하지 않고 바로 DLT로 전송
        errorHandler.addNotRetryableExceptions(
                IllegalArgumentException.class,
                JsonProcessingException.class
        );

        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> dlqListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(1); // DLQ는 순차 처리
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);

        // DLQ에서는 추가 재시도 없음 (이미 재시도를 다 한 상태)
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
                new FixedBackOff(0L, 0) // 재시도 없음
        );

        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }

//    public <T extends SpecificRecord> ConsumerFactory<String, T> avroConsumerFactory(Class<T> clazz) {
//        Map<String, Object> props = new HashMap<>();
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
//        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
//        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
//        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
//
//        // Schema Registry 주소
//        props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);
////
//        // Avro 클래스로 deserialize 하려면 필수!
//        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);
//
//        return new DefaultKafkaConsumerFactory<>(props);
//    }
//
//    @Bean
//    public ConcurrentKafkaListenerContainerFactory<String, CouponIssueEvent> couponRequestEventListenerFactory() {
//        ConcurrentKafkaListenerContainerFactory<String, CouponIssueEvent> factory =
//                new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(this.<CouponIssueEvent>avroConsumerFactory());
//        return factory;
//    }
>>>>>>> origin
}
