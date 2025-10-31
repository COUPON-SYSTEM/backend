//package com.company.demo.giftcoupon.producer;
//
//import com.company.demo.common.constant.KafkaTopic;
//import com.company.demo.common.client.CustomKafkaProducer;
//import com.company.demo.giftcoupon.event.CouponIssueEvent;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.*;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.kafka.core.KafkaTemplate;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//
//@SpringBootTest
//public class KafkaProducerTest {
//
//    @Mock
//    private KafkaTemplate<String, String> kafkaTemplate;
//
//    @Mock
//    private KafkaTemplate<String, CouponIssueEvent> giftKafkaTemplate;
//
//    @InjectMocks
//    private CustomKafkaProducer kafkaProducer;
//
//    @Captor
//    private ArgumentCaptor<CouponIssueEvent> eventCaptor;
//
//    @Test
//    @DisplayName("테스트 토픽으로 메시지 보내기")
//    void testSendMessage() {
//        // Given
//        String topic = "test-topic";
//        String message = "Hello, Kafka!";
//
//        // When
//        kafkaProducer.sendMessage(topic, message);
//
//        // Then
//        verify(kafkaTemplate, times(1)).send(topic, message);
//    }
//
//    @Test
//    @DisplayName("쿠폰 요청 메시지 보내기")
//    void testSendGiftRequestMessage() {
//        // Given
//        String topic = KafkaTopic.COUPON_ISSUE;
//        CouponIssueEvent event = CouponIssueEvent.builder()
//                .memberId("1")
//                .build();
//
//        // When
//        //kafkaProducer.sendRequestMessage(event);
//
//        // Then
//        verify(giftKafkaTemplate, times(1)).send(eq(topic), eventCaptor.capture());
//
//        CouponIssueEvent captured = eventCaptor.getValue();
//        assertEquals(event.memberId(), captured.memberId()); // JSON으로 직렬화되더라도 객체 내용 비교 가능
//    }
//}