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

    private final KafkaTemplate<String, DomainEventEnvelope<?>> dltKafkaTemplate;

    @Bean
    public ConsumerFactory<String, DomainEventEnvelope<?>> couponIssuedConsumerFactory() {
        log.info(">>> couponIssuedConsumerFactory 생성됨");
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, "10000");
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, "300000");

        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        JsonDeserializer<DomainEventEnvelope<?>> valueDeserializer =
                new JsonDeserializer<>();

        valueDeserializer.addTrustedPackages("*");
        valueDeserializer.setRemoveTypeHeaders(false);
        valueDeserializer.setUseTypeMapperForKey(false);

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
    public ConcurrentKafkaListenerContainerFactory<String, DomainEventEnvelope<?>>
    couponIssueKafkaListenerContainerFactory(
            ConsumerFactory<String, DomainEventEnvelope<?>> couponIssuedConsumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, DomainEventEnvelope<?>> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(couponIssuedConsumerFactory);
        factory.setConcurrency(1);

        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);

        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                dltKafkaTemplate,
                (record, exception) -> {
                    String dltTopicName = record.topic() + ".DLT";
                    log.warn(
                            "=== DLT로 전송 ===\n" +
                                    "원본토픽: {}\n" +
                                    "DLT토픽: {}\n" +
                                    "Offset: {}\n" +
                                    "Key: {}\n" +
                                    "Value: {}\n" +
                                    "오류: {}",
                            record.topic(),
                            dltTopicName,
                            record.offset(),
                            record.key(),
                            record.value(),
                            exception.getMessage()
                    );
                    return new TopicPartition(dltTopicName, 0);
                }
        );

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
                recoverer,
                new FixedBackOff(1000L, 3)
        );

        errorHandler.setCommitRecovered(true);     // DLT 전송 성공 시 원본 offset 커밋
        errorHandler.setSeekAfterError(false);     // 에러 후 같은 레코드 다시 안 읽음
        errorHandler.setAckAfterHandle(true);      // 처리 후 ack

        errorHandler.addNotRetryableExceptions(
                IllegalArgumentException.class,
                JsonProcessingException.class,
                NullPointerException.class
        );

        factory.setCommonErrorHandler(errorHandler);

        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, DomainEventEnvelope<?>>
    dlqListenerContainerFactory(
            ConsumerFactory<String, DomainEventEnvelope<?>> couponIssuedConsumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, DomainEventEnvelope<?>> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(couponIssuedConsumerFactory);
        factory.setConcurrency(1);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
                (record, exception) -> {
                    log.error("=== DLT 메시지 처리 실패 (무시됨) ===\n" +
                                    "Topic: {}\n" +
                                    "Offset: {}\n" +
                                    "Error: {}",
                            record.topic(),
                            record.offset(),
                            exception.getMessage());
                },
                new FixedBackOff(0L, 0L)
        );

        errorHandler.setCommitRecovered(true);
        errorHandler.setSeekAfterError(false);
        errorHandler.setAckAfterHandle(true);

        errorHandler.addNotRetryableExceptions(
                Exception.class
        );
        factory.setCommonErrorHandler(errorHandler);

        return factory;
    }
}