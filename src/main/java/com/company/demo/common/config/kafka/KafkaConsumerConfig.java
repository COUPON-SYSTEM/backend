package com.company.demo.common.config.kafka;

import com.company.demo.giftcoupon.event.CouponIssueEvent;
import com.company.demo.giftcoupon.event.CouponIssuePayload;
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

    //x@Value("${spring.kafka.properties.schema.registry.url}")
    // private String schemaRegistryUrl;

    // private final KafkaErrorHandler kafkaErrorHandler;

    private Map<String, Object> consumerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "consumer-group1");     // 그룹 지정
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");   // 필요에 맞게 latest/earliest
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        // JsonDeserializer 옵션 (패키지 신뢰 + 타입정보 헤더 사용)
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.company.demo.*");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, true); // Producer가 타입 헤더를 넣는다는 가정

        // 만약 Producer에서 타입 헤더를 안 넣는다면(default type 지정)
        // props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        // props.put(JsonDeserializer.VALUE_DEFAULT_TYPE,
        //     "com.company.demo.giftcoupon.outbox.domain.event.DomainEventEnvelope");

        return props;
    }

    public <T> ConsumerFactory<String, T> consumerFactory(Class<T> clazz) {
        JsonDeserializer<T> deserializer = new JsonDeserializer<>(clazz);
        deserializer.addTrustedPackages("*");

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    /** String -> DomainEventEnvelope<CouponIssuePayload> 용 컨슈머 팩토리 */
    @Bean
    public ConsumerFactory<String, DomainEventEnvelope<CouponIssuePayload>> couponIssueConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                consumerProps(),
                new StringDeserializer(),
                new JsonDeserializer<>(DomainEventEnvelope.class) // 제네릭 소거 → Envelope 기준 역직렬화
        );
    }

    /** @KafkaListener 에서 사용할 리스너 컨테이너 팩토리 */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, DomainEventEnvelope<CouponIssuePayload>>
    couponIssueKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, DomainEventEnvelope<CouponIssuePayload>> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(couponIssueConsumerFactory());
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
}
