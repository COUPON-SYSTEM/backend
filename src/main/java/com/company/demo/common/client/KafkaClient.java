package com.company.demo.common.client;


import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.errors.TopicExistsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
public class KafkaClient {

    private final AdminClient adminClient;

    public KafkaClient(@Value("${kafka.bootstrap-servers:localhost:9092}") String bootstrapServers) {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        this.adminClient = AdminClient.create(props);
    }

    public void createTopicIfNotExists(String topicName, int partitions, short replicationFactor) {
        NewTopic newTopic = new NewTopic(topicName, partitions, replicationFactor);  // 토픽 이름, 파티션 수, Replica 수
        // if, 3이라면 각 파티션을 3개의 브로커에 복사하겠다는 의미이다.
        CreateTopicsResult result = adminClient.createTopics(Collections.singletonList(newTopic));
        try {
            result.all().get(); // 동기화
            log.info("토픽 생성 성공: {}", topicName);
        } catch (InterruptedException e) { // 블락되어 있을 때, 인터럽트 발생시
            Thread.currentThread().interrupt(); // 인터럽트 복원
            log.warn("토픽 생성 중 인터럽트 발생: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        } catch (ExecutionException e) { // 이게 기본 실행 오류
            if (e.getCause() instanceof TopicExistsException) {
                log.info("이미 존재하는 토픽: {}", topicName);
                throw new RuntimeException(e);
            } else {
                log.error("토픽 생성 실패 - {}", e.getCause().toString(), e);
                throw new RuntimeException(e);
            }
        }
    }
}
