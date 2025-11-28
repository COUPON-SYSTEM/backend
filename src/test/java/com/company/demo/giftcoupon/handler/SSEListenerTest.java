package com.company.demo.giftcoupon.handler;

import com.company.demo.common.constant.KafkaTopic;
import com.company.demo.giftcoupon.domain.repository.SseEmitterRepository;
import com.company.demo.giftcoupon.outbox.domain.event.CouponIssuedPayload;
import com.company.demo.giftcoupon.outbox.domain.event.DomainEventEnvelope;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@EmbeddedKafka(
        partitions = 1,
        topics = {KafkaTopic.COUPON_ISSUED, KafkaTopic.COUPON_ISSUED + ".DLT"},
        brokerProperties = {
                "listeners=PLAINTEXT://localhost:9092",
                "port=9092"
        }
)
@ActiveProfiles("test")
@DirtiesContext
class SSEListenerTest {

    @Autowired
    private KafkaTemplate<String, DomainEventEnvelope<CouponIssuedPayload>> couponIssuedKafkaTemplate;

    @MockitoBean
    private SseEmitterRepository sseEmitterRepository;

    @MockitoBean
    private FirebaseApp firebaseApp;

    @MockitoBean
    private FirebaseMessaging firebaseMessaging;

    private static final Long USER_ID = 123L;
    private static final Long COUPON_ID = 999L;
    private static final Long PROMOTION_ID = 1L;
    private static final String EVENT_TYPE = "coupon-issued";
    private static final String SOURCE = "giftcoupon-service";

    @BeforeEach
    void setUp() {
        reset(sseEmitterRepository);
    }

    @Test
    @DisplayName("SSE 연결이 있으면 emitter.send()가 호출된다")
    void sendsToExistingEmitter() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        SseEmitter emitter = mock(SseEmitter.class);

        when(sseEmitterRepository.findById(USER_ID)).thenReturn(emitter);

        doAnswer(inv -> { latch.countDown(); return null; })
                .when(emitter)
                .send(any(SseEmitter.SseEventBuilder.class));

        publishCouponIssued(USER_ID, COUPON_ID, PROMOTION_ID);

        assertThat(latch.await(5, TimeUnit.SECONDS)).isTrue();
        verify(sseEmitterRepository, atLeastOnce()).findById(USER_ID);
        verify(emitter, times(1)).send(any(SseEmitter.SseEventBuilder.class));
    }

    @Test
    @DisplayName("SSE 연결이 없으면 아무 것도 보내지 않고 삭제도 하지 않는다")
    void ignoresWhenNoEmitter() throws Exception {
        when(sseEmitterRepository.findById(USER_ID)).thenReturn(null);

        publishCouponIssued(USER_ID, COUPON_ID, PROMOTION_ID);

        Thread.sleep(200);
        verify(sseEmitterRepository, atLeastOnce()).findById(USER_ID);
        verify(sseEmitterRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("emitter.send() 중 IOException이 터지면 emitter를 제거한다")
    void removesEmitterOnSendFailure() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        SseEmitter failingEmitter = mock(SseEmitter.class);

        when(sseEmitterRepository.findById(USER_ID)).thenReturn(failingEmitter);

        doAnswer(inv -> {
            latch.countDown();
            throw new IOException("disconnected");
        }).when(failingEmitter).send(any(SseEmitter.SseEventBuilder.class));

        publishCouponIssued(USER_ID, COUPON_ID, PROMOTION_ID);

        assertThat(latch.await(5, TimeUnit.SECONDS)).isTrue();

        Thread.sleep(200);
        verify(sseEmitterRepository, atLeastOnce()).deleteById(USER_ID);
    }

    @Test
    @DisplayName("여러 사용자 각각에게 메시지를 독립적으로 전송할 수 있다")
    void broadcastsToMultipleUsers() throws Exception {
        Long u1 = 1L;
        Long u2 = 2L;
        Long u3 = 3L;

        SseEmitter e1 = mock(SseEmitter.class);
        SseEmitter e2 = mock(SseEmitter.class);
        SseEmitter e3 = mock(SseEmitter.class);

        CountDownLatch latch = new CountDownLatch(3);

        when(sseEmitterRepository.findById(u1)).thenReturn(e1);
        when(sseEmitterRepository.findById(u2)).thenReturn(e2);
        when(sseEmitterRepository.findById(u3)).thenReturn(e3);

        doAnswer(inv -> { latch.countDown(); return null; })
                .when(e1).send(any(SseEmitter.SseEventBuilder.class));
        doAnswer(inv -> { latch.countDown(); return null; })
                .when(e2).send(any(SseEmitter.SseEventBuilder.class));
        doAnswer(inv -> { latch.countDown(); return null; })
                .when(e3).send(any(SseEmitter.SseEventBuilder.class));

        publishCouponIssued(u1, 101L, 1L);
        publishCouponIssued(u2, 102L, 1L);
        publishCouponIssued(u3, 103L, 1L);

        assertThat(latch.await(5, TimeUnit.SECONDS)).isTrue();

        verify(e1, times(1)).send(any(SseEmitter.SseEventBuilder.class));
        verify(e2, times(1)).send(any(SseEmitter.SseEventBuilder.class));
        verify(e3, times(1)).send(any(SseEmitter.SseEventBuilder.class));
    }

    private void publishCouponIssued(Long userId, Long couponId, Long promotionId) {
        CouponIssuedPayload payload = CouponIssuedPayload.of(
                userId,
                couponId,
                LocalDateTime.now(),
                promotionId
        );

        DomainEventEnvelope<CouponIssuedPayload> envelope = DomainEventEnvelope.of(
                UUID.randomUUID().toString(),
                EVENT_TYPE,
                SOURCE,
                payload
        );

        couponIssuedKafkaTemplate.send(KafkaTopic.COUPON_ISSUED, envelope);
    }
}
