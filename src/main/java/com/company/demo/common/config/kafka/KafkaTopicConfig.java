package com.company.demo.common.config.kafka;

import com.company.demo.common.constant.KafkaTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public KafkaAdmin.NewTopics couponRequestTopics() {
        return new KafkaAdmin.NewTopics(
                TopicBuilder.name(KafkaTopic.COUPON_ISSUE)
                        .partitions(3)
                        .replicas(1)
                        .config(TopicConfig.RETENTION_MS_CONFIG, String.valueOf(1000 * 60 * 60)) // 보관 기간 1시간
                        .build()
        );
    }
}
