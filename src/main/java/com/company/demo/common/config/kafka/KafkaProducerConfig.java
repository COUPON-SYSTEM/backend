package com.company.demo.common.config.kafka;

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
import com.company.demo.giftcoupon.event.CouponIssueEvent;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class KafkaProducerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String BOOTSTRAP_SERVERS;

    // 1. 기본 프로듀서 설정 맵 생성
    private Map<String, Object> baseProducerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 10);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        return props;
    }

    // 2. String 타입을 위한 ProducerFactory (JSON 직렬화가 필요 없으므로 StringSerializer 사용 가능)
    @Bean
    public ProducerFactory<String, String> stringProducerFactory() {
        Map<String, Object> props = baseProducerProps();
        // String은 단순 문자열이므로 JsonSerializer 대신 StringSerializer 사용이 효율적
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    // 3. Object(Event) 타입을 위한 ProducerFactory (JsonSerializer 사용)
    @Bean
    public ProducerFactory<String, Object> jsonProducerFactory() {
        Map<String, Object> props = baseProducerProps();

        // **JsonSerializer를 명시적으로 사용하고 설정을 추가하여 타입 정보를 제공합니다.**
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        // 컨슈머가 역직렬화할 때 필요한 정보를 헤더에 넣지 않도록 설정 (선택적)
        // props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);

        return new DefaultKafkaProducerFactory<>(props);
    }

    // --- KafkaTemplate 빈 정의 ---

    @Bean
    @Qualifier("stringKafkaTemplate")
    public KafkaTemplate<String, String> stringKafkaTemplate() { // 이름 변경으로 명확성 증가
        return new KafkaTemplate<>(stringProducerFactory());
    }

    @Bean
    @Qualifier("giftCouponKafkaTemplate")
    public KafkaTemplate<String, Object> giftCouponKafkaTemplate() {
        // Object 타입을 처리하는 jsonProducerFactory를 사용
        return new KafkaTemplate<>(jsonProducerFactory(), true);
    }
}
