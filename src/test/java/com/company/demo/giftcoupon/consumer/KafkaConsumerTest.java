package com.company.demo.giftcoupon.consumer;

import com.company.demo.giftcoupon.producer.CustomKafkaProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.kafka.test.context.EmbeddedKafka;


@SpringBootTest
@ExtendWith(OutputCaptureExtension.class)
@EmbeddedKafka(partitions = 1, topics = "test-topic", brokerProperties = {
        "listeners=PLAINTEXT://localhost:9092", "port=9092"
})
public class KafkaConsumerTest {

    @Autowired
    private CustomKafkaProducer customKafkaProducer;

    @BeforeEach
    void setUp() {
        customKafkaProducer.sendMessage("test-topic","hello test");
    }

    @Test
    @DisplayName("프로듀서가 보낸 메시지 1개 수신")
    void testConsumerLogsMessage(CapturedOutput output) throws InterruptedException {

        // 메시지 처리 시간 기다리기
        Thread.sleep(1000);

        // 로그에 메시지가 포함됐는지 검증
        assert output.getOut().contains("Received message: hello test");
    }
}
