package com.company.demo.giftcoupon.handler;

import com.company.demo.common.constant.GroupType;
import com.company.demo.common.constant.KafkaTopic;
import com.company.demo.giftcoupon.domain.repository.SseEmitterRepository;
import com.company.demo.giftcoupon.event.CouponIssuedEvent;
import com.company.demo.giftcoupon.handler.CouponEventHandler;
import com.company.demo.giftcoupon.outbox.domain.event.CouponIssuedPayload;
import com.company.demo.giftcoupon.outbox.domain.event.DomainEventEnvelope;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class SSEListener implements CouponEventHandler {

    private final SseEmitterRepository sseEmitterRepository;

    @Override
    @KafkaListener(
            topics = KafkaTopic.COUPON_ISSUED,
            groupId = GroupType.SSE,
            containerFactory = "couponIssueKafkaListenerContainerFactory"
    )
    public void handle(@Payload DomainEventEnvelope<CouponIssuedPayload> envelope){
        pushSSE(envelope);
    }

    private void pushSSE(DomainEventEnvelope<CouponIssuedPayload> envelope) {

        if (envelope == null || envelope.payload() == null) {
            log.warn("잘못된 Kafka 메시지 수신 - envelope 또는 payload가 null: {}", envelope);
            return;
        }

        CouponIssuedPayload payload = envelope.payload();
        log.info("payload log: {}", payload);
        Long userId = payload.userId();
        SseEmitter emitter = sseEmitterRepository.findById(userId);

        if (emitter == null) {
            log.debug("SSE Emitter를 찾을 수 없음 - UserId: {} (사용자가 연결하지 않았거나 이미 종료됨)", userId);
            return;
        }

        try {
            emitter.send(SseEmitter.event()
                    .name("couponIssued")
                    .data(payload)
            );
            log.info("SSE 데이터 전송 성공 - UserId: {}, CouponId: {}", userId, payload.couponId());
        } catch (IOException e) {
            log.warn("SSE 데이터 전송 실패 (연결 끊김) - UserId: {}", userId, e);
            sseEmitterRepository.deleteById(userId);
        } catch (Exception e) {
            log.error("SSE 데이터 전송 중 예상치 못한 오류 - UserId: {}", userId, e);
            sseEmitterRepository.deleteById(userId);
        }
    }
}
