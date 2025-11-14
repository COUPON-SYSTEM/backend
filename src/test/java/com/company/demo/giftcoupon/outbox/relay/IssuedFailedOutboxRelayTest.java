package com.company.demo.giftcoupon.outbox.relay;

import com.company.demo.common.client.CustomKafkaProducer;
import com.company.demo.giftcoupon.outbox.domain.entity.CouponIssuanceOutboxEvent;
import com.company.demo.giftcoupon.outbox.domain.event.CouponIssuedPayload;
import com.company.demo.giftcoupon.outbox.domain.event.DomainEventEnvelope;
import com.company.demo.giftcoupon.outbox.domain.repository.CouponIssuanceOutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IssuedFailedOutboxRelayTest {

    @Mock
    private CouponIssuanceOutboxRepository outboxRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private CustomKafkaProducer producer;

    private IssuedFailedOutboxRelay relay;

    @BeforeEach
    void setUp() {
        relay = new IssuedFailedOutboxRelay(outboxRepository, objectMapper, producer);
    }

    @Test
    @DisplayName("아웃박스 테이블가 비어있을 시 아무것도 하지 않음")
    void relay_whenNoUnpublished_thenDoNothing() {
        // given
        when(outboxRepository.claimOneUnpublishedForRetry(any(LocalDateTime.class)))
                .thenReturn(Optional.empty());


        // when
        relay.relay();


        // then
        verifyNoInteractions(objectMapper, producer);
    }

    @Test
    @DisplayName("아웃박스을 클레임해서 퍼블리쉬하기")
    void relay_whenSuccess_thenPublishAndMarkPublished() throws Exception {
        // given
        CouponIssuanceOutboxEvent entity = mock(CouponIssuanceOutboxEvent.class);
        when(outboxRepository.claimOneUnpublishedForRetry(any(LocalDateTime.class)))
                .thenReturn(Optional.of(entity));

        when(entity.getEventId()).thenReturn("evt-123");
        when(entity.getEventType()).thenReturn("COUPON_ISSUANCE_SERVICE");
        when(entity.getSource()).thenReturn("giftcoupon");
        when(entity.getPayload()).thenReturn("{\"userId\":1,\"couponId\":10,\"issuedAt\":\"2025-11-12T09:00:00\"}");

        CouponIssuedPayload payload = CouponIssuedPayload.of(1L, 10L, LocalDateTime.parse("2025-11-12T09:00:00"), 3L);
        when(objectMapper.readValue(anyString(), eq(CouponIssuedPayload.class))).thenReturn(payload);

        ArgumentCaptor<DomainEventEnvelope<CouponIssuedPayload>> captor =
                ArgumentCaptor.forClass((Class) DomainEventEnvelope.class);

        // when
        relay.relay();

        // then: 카프카 전송 호출됨
        verify(producer, times(1)).sendIssuedMessage(captor.capture());

        DomainEventEnvelope<CouponIssuedPayload> sent = captor.getValue();
        // 간단 무결성 체크
        Assertions.assertEquals("evt-123", sent.eventId());
        Assertions.assertEquals("COUPON_ISSUANCE_SERVICE", sent.eventType());
        Assertions.assertEquals("giftcoupon", sent.source());
        Assertions.assertEquals(10L, sent.payload().couponId());
        Assertions.assertEquals(3L, sent.payload().promotionId());

        // then: 성공 마킹 호출됨
        verify(entity, times(1)).markPublished();
    }

    @Test
    @DisplayName("아웃박스의 페이로드 파싱 중 에러 → is_published는 false 유지")
    void relay_whenJsonParseFails_thenDoNotPublishNorMark_andPublishedRemainsFalse() throws Exception {
        // given: 실제 엔티티 생성(스파이)
        CouponIssuanceOutboxEvent entity = spy(
                CouponIssuanceOutboxEvent.builder()
                        .id(1L)
                        .eventId("evt-parse-fail")
                        .eventType("COUPON_ISSUANCE_SERVICE")
                        .source("giftcoupon")
                        .payload("malformed-json")
                        .createdAt(LocalDateTime.now().minusMinutes(1))
                        .published(false)
                        .build()
        );


        // when: 아웃박스 엔티티 하나 클레임되는 지 검증
        when(outboxRepository.claimOneUnpublishedForRetry(any(LocalDateTime.class)))
                .thenReturn(Optional.of(entity));
        // JSON 파싱 실패 유도
        when(objectMapper.readValue(anyString(), eq(CouponIssuedPayload.class)))
                .thenThrow(new RuntimeException("parse error"));
        relay.relay();


        // then: 실패 시 상태 유지 확인
        verifyNoInteractions(producer);
        verify(entity, never()).markPublished();
        Assertions.assertFalse(entity.isPublished(), "published 플래그가 false여야 한다");
    }

    @Test
    @DisplayName("퍼블리시 중 에러 → is_published는 false 유지")
    void relay_whenKafkaSendThrows_thenDoNotMarkPublished_andPublishedRemainsFalse() throws Exception {
        // given: 실제 엔티티 생성(스파이)
        CouponIssuanceOutboxEvent entity = spy(
                CouponIssuanceOutboxEvent.builder()
                        .id(2L)
                        .eventId("evt-broker-down")
                        .eventType("COUPON_ISSUANCE_SERVICE")
                        .source("giftcoupon")
                        .payload("{\"userId\":2,\"couponId\":20,\"issuedAt\":\"2025-11-12T09:05:00\"}")
                        .createdAt(LocalDateTime.now().minusMinutes(1))
                        .published(false)
                        .build()
        );


        // when: 아웃박스 엔티티 하나 클레임되는 지 검증
        when(outboxRepository.claimOneUnpublishedForRetry(any(LocalDateTime.class)))
                .thenReturn(Optional.of(entity));
        // JSON 정상 파싱
        CouponIssuedPayload payload = CouponIssuedPayload.of(
                2L, 20L, LocalDateTime.parse("2025-11-12T09:05:00"), 3L);
        when(objectMapper.readValue(anyString(), eq(CouponIssuedPayload.class))).thenReturn(payload);
        // 카프카 전송 예외 유도
        doThrow(new RuntimeException("broker down"))
                .when(producer).sendIssuedMessage(any(DomainEventEnvelope.class));
        relay.relay();


        // then: 실패 시 상태 유지 확인
        verify(producer, times(1)).sendIssuedMessage(any());
        verify(entity, never()).markPublished();
        Assertions.assertFalse(entity.isPublished(), "published 플래그가 false여야 한다");
    }

    @Test
    @DisplayName("이전 실행에서 실패한 아웃박스 이벤트가 다음 실행에서 재시도되어 성공적으로 퍼블리시됨")
    void relay_whenFirstAttemptFails_thenRetryAndPublishSuccessfully() throws Exception {
        // given: 실제 엔티티 생성(스파이)
        CouponIssuanceOutboxEvent entity = spy(
                CouponIssuanceOutboxEvent.builder()
                        .id(3L)
                        .eventId("evt-retry-success")
                        .eventType("COUPON_ISSUANCE_SERVICE")
                        .source("giftcoupon")
                        .payload("{\"userId\":3,\"couponId\":30,\"issuedAt\":\"2025-11-12T09:10:00\"}")
                        .createdAt(LocalDateTime.now().minusMinutes(1))
                        .published(false)
                        .build()
        );

        // 아웃박스에서 같은 이벤트를 계속 클레임한다고 가정 (1차 시도, 2차 시도 모두 동일 엔티티 반환)
        when(outboxRepository.claimOneUnpublishedForRetry(any(LocalDateTime.class)))
                .thenReturn(Optional.of(entity));

        // JSON 파싱은 첫 번째 호출에서는 실패, 두 번째 호출에서는 성공하도록 설정
        CouponIssuedPayload payload = CouponIssuedPayload.of(
                3L,
                30L,
                LocalDateTime.parse("2025-11-12T09:10:00"),
                5L
        );

        when(objectMapper.readValue(anyString(), eq(CouponIssuedPayload.class)))
                .thenThrow(new RuntimeException("temporary parse error")) // 1차 배치 실행 시
                .thenReturn(payload);                                    // 2차 배치 실행 시

        // when 1차 실행: 파싱 에러 발생 → publish / markPublished 둘 다 안 됨
        relay.relay();

        // then (1차 실행 결과 확인)
        verify(producer, times(0)).sendIssuedMessage(any());
        verify(entity, never()).markPublished();
        Assertions.assertFalse(entity.isPublished(), "1차 시도 후에도 published는 false 여야 한다");

        // when 2차 실행: 재시도 시에는 파싱 성공 + 카프카 전송 성공
        relay.relay();

        // then (2차 실행 결과 확인)
        verify(producer, times(1)).sendIssuedMessage(any(DomainEventEnvelope.class));
        verify(entity, times(1)).markPublished();
        Assertions.assertTrue(entity.isPublished(), "2차 시도 후에는 published가 true 여야 한다");
    }
}
