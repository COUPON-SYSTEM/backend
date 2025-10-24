package com.company.demo.giftcoupon.handler;

import com.company.demo.common.constant.GroupType;
import com.company.demo.common.constant.KafkaTopic;
import com.company.demo.giftcoupon.domain.repository.SseEmitterRepository;
import com.company.demo.giftcoupon.event.CouponIssuedEvent;
import com.company.demo.giftcoupon.handler.CouponEventHandler;
import com.company.demo.giftcoupon.outbox.domain.event.CouponIssuedPayload;
import com.company.demo.giftcoupon.outbox.domain.event.DomainEventEnvelope;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
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
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handle(DomainEventEnvelope<CouponIssuedPayload> envelope) throws IOException {
        pushSSE(envelope);
    }

    private void pushSSE (DomainEventEnvelope<CouponIssuedPayload> envelope) throws IOException {
        CouponIssuedPayload payload = envelope.payload();
        SseEmitter emitter = sseEmitterRepository.findById(payload.userId());

        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("couponIssued")
                        .data(payload)
                );
                log.info("SSE 데이터 전송 성공 - UserId: {}", payload.userId());
            } catch (Exception e) {
                log.error("SSE 데이터 전송 실패 - UserId: {}", payload.userId(), e.getMessage(), e);
                sseEmitterRepository.deleteById(payload.userId());
                // SSE 전송 실패처럼 단순히 실시간 푸시가 안 된 상황은 DLQ로 보내기보다 해당 리스너에서 흡수 처리하는 것이 효율적임
            }
        }
    }
}
