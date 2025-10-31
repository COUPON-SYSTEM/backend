package com.company.demo.common.config.kafka;

import com.company.demo.giftcoupon.outbox.domain.event.CouponIssuedPayload;
import com.company.demo.giftcoupon.outbox.domain.event.DomainEventEnvelope;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Qualifier;
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
    private Map<String, Object> baseProducerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 10);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return props;
    }

    /** Test 용 팩토리 */
    @Bean
    public ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(baseProducerConfigs());
    }

    // 실제로 Kafka에 메시지를 전송하는 데 사용하는 스프링 추상화 객체: KafkaTemplates
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    /** String -> DomainEventEnvelope<CouponIssuedPayload> 용 */
    @Bean
    public ProducerFactory<String, DomainEventEnvelope<CouponIssuedPayload>> couponIssuedProducerFactory() {
        return new DefaultKafkaProducerFactory<>(baseProducerConfigs());
    }

    @Bean
    public KafkaTemplate<String, DomainEventEnvelope<CouponIssuedPayload>> issuedKafkaTemplate() {
        return new KafkaTemplate<>(couponIssuedProducerFactory());
    }

    // 2. String 타입을 위한 ProducerFactory (JSON 직렬화가 필요 없으므로 StringSerializer 사용 가능)
    @Bean
    public ProducerFactory<String, String> stringProducerFactory() {
        Map<String, Object> props = baseProducerConfigs();
        // String은 단순 문자열이므로 JsonSerializer 대신 StringSerializer 사용이 효율적
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    // 3. Object(Event) 타입을 위한 ProducerFactory (JsonSerializer 사용)
    @Bean
    public ProducerFactory<String, Object> jsonProducerFactory() {
        Map<String, Object> props = baseProducerConfigs();

        // **JsonSerializer를 명시적으로 사용하고 설정을 추가하여 타입 정보를 제공합니다.**
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        // 컨슈머가 역직렬화할 때 필요한 정보를 헤더에 넣지 않도록 설정 (선택적)
        // props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);

        return new DefaultKafkaProducerFactory<>(props);
    }
}