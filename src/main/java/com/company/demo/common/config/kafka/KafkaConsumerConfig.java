package com.company.demo.common.config.kafka;

import com.company.demo.giftcoupon.outbox.domain.event.CouponIssuedPayload;
import com.company.demo.giftcoupon.outbox.domain.event.DomainEventEnvelope;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference; // HEAD 내용
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

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

    // KafkaTemplate<String, DomainEventEnvelope<CouponIssuedPayload>>를 사용
    private final KafkaTemplate<String, DomainEventEnvelope<CouponIssuedPayload>> kafkaTemplate;

    /**
     * coupon-issued 같은 비즈니스 이벤트 메시지를 역직렬화할 ConsumerFactory.
     * value 타입은 DomainEventEnvelope<?> 로 잡는다.
     *
     * 주의:
     * - JsonDeserializer에 DomainEventEnvelope.class만 전달하면
     * 내부 payload는 CouponIssuedPayload로 바로 변환되지 않고 LinkedHashMap일 수 있다.
     * 그 부분은 @KafkaListener 쪽에서 ObjectMapper.convertValue(...)로 처리한다.
     */
    @Bean
    public ConsumerFactory<String, DomainEventEnvelope<CouponIssuedPayload>> couponIssuedConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        // TypeReference를 사용하여 제네릭 타입 정보를 유지하며 역직렬화 설정
        TypeReference<DomainEventEnvelope<CouponIssuedPayload>> typeRef =
                new TypeReference<DomainEventEnvelope<CouponIssuedPayload>>() {};

        JsonDeserializer<DomainEventEnvelope<CouponIssuedPayload>> valueDeserializer =
                new JsonDeserializer<>(typeRef, false);

        valueDeserializer.addTrustedPackages("com.company.demo.*");

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                valueDeserializer
        );
    }


    /**
     * 실제 @KafkaListener(containerFactory="couponIssueKafkaListenerContainerFactory")
     * 에서 사용할 리스너 컨테이너 팩토리.
     *
     * - poll 스레드를 띄우고
     * - record 단위 ack 하고
     * - 예외 시 재시도 & DLQ 전송 로직까지 세팅한다.
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, DomainEventEnvelope<CouponIssuedPayload>>
    couponIssueKafkaListenerContainerFactory(
            ConsumerFactory<String, DomainEventEnvelope<CouponIssuedPayload>> couponIssuedConsumerFactory
    ) {

        ConcurrentKafkaListenerContainerFactory<String, DomainEventEnvelope<CouponIssuedPayload>> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(couponIssuedConsumerFactory);
        factory.setConcurrency(1); // 동시 컨슈머 스레드 수. 필요하면 늘릴 수 있음.
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);

        /*
         * 실패 처리 전략
         *
         * - Listener에서 예외가 터지면 DefaultErrorHandler가 개입.
         * - 1초 간격으로 3번 재시도.
         * - 그래도 실패하면 DeadLetterPublishingRecoverer가 원본토픽.DLT 로 메시지를 보냄.
         */
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (record, exception) -> {
                    String dltTopicName = record.topic() + ".DLT";
                    log.warn(
                            "DLT로 전송: 원본토픽={}, DLT토픽={}, 오류={}",
                            record.topic(),
                            dltTopicName,
                            exception.getMessage()
                    );
                    return new TopicPartition(dltTopicName, 0);
                }
        );

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
                recoverer,
                new FixedBackOff(1000L, 3) // 1초 간격, 최대 3회 재시도
        );

        // 이런 예외는 재시도 없이 바로 DLT
        errorHandler.addNotRetryableExceptions(
                IllegalArgumentException.class,
                JsonProcessingException.class
        );

        factory.setCommonErrorHandler(errorHandler);

        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, DomainEventEnvelope<CouponIssuedPayload>>
    dlqListenerContainerFactory(
            ConsumerFactory<String, DomainEventEnvelope<CouponIssuedPayload>> couponIssuedConsumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, DomainEventEnvelope<CouponIssuedPayload>> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(couponIssuedConsumerFactory);
        factory.setConcurrency(1); // DLQ 처리는 보통 순차적으로 (1개 스레드) 처리

        // DLQ는 최종 실패 메시지이므로, AckMode.RECORD를 사용하되,
        // 리스너에서 예외가 발생하면 Spring Kafka의 기본 동작(재시도 없음)을 따르도록 합니다.
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);

        // DLQ 리스너는 재시도나 추가적인 DLT 전송 로직이 필요 없습니다.
        // 따라서 별도의 DefaultErrorHandler를 설정하지 않습니다.
        // 만약 예외 발생 시 바로 DLT 처리를 원한다면, 아래와 같이 SimpleErrorHandler를 설정할 수 있습니다.

        // factory.setCommonErrorHandler(new DefaultErrorHandler(new FixedBackOff(0L, 0)));
        // 위 코드는 예외 발생 시 재시도 없이 바로 실패 처리합니다. (DLQ에서는 일반적으로 충분)

        return factory;
    }
}