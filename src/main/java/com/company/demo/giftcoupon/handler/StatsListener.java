package com.company.demo.giftcoupon.handler;

import com.company.demo.common.constant.GroupType;
import com.company.demo.common.constant.KafkaTopic;
import com.company.demo.giftcoupon.domain.repository.SseEmitterRepository;
import com.company.demo.giftcoupon.event.CouponIssuedEvent;
import com.company.demo.giftcoupon.handler.CouponEventHandler;
import com.company.demo.giftcoupon.handler.sevice.StatisticsService;
import com.company.demo.giftcoupon.mapper.dto.StatisticsDto;
import com.company.demo.giftcoupon.outbox.domain.event.CouponIssuedPayload;
import com.company.demo.giftcoupon.outbox.domain.event.DomainEventEnvelope;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class StatsListener implements CouponEventHandler { // 쿠폰 발급에 따른 통계

    private final SseEmitterRepository sseEmitterRepository;
    private final StatisticsService statisticsService;

    @Override
    @KafkaListener(
            topics = KafkaTopic.COUPON_ISSUED,
            groupId = GroupType.EXTERNAL,
            containerFactory = "couponIssueKafkaListenerContainerFactory"
    )
    public void handle(DomainEventEnvelope<CouponIssuedPayload> envelope) {
        processAndSendStats(envelope);
    }

    private void processAndSendStats(DomainEventEnvelope<CouponIssuedPayload> envelope){
        CouponIssuedPayload payload = envelope.payload();

        StatisticsDto updatedStats = statisticsService.processIssuedEvent(payload);

        // 발행자 ID (Publisher ID)를 통해 SSE Emitter 조회 (쿠폰 발행자를 식별해야 함)
        Long publisherId = updatedStats.publisherId(); // updatedStats에서 발행자 ID를 가져온다고 가정

        SseEmitter emitter = sseEmitterRepository.findById(publisherId);

        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("coupon-stats-update")
                        .data(updatedStats) // 계산된 통계 객체를 JSON 형태로 전송
                );
                log.info("실시간 통계 전송 성공 - PublisherId: {}", publisherId);
            } catch (IOException e) {
                sseEmitterRepository.deleteById(publisherId);
                log.error("SSE 전송 실패, Emitter 제거: {}", publisherId, e);
            }
        }
    }
}
