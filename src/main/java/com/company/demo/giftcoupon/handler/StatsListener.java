package com.company.demo.giftcoupon.handler;

import com.company.demo.common.constant.GroupType;
import com.company.demo.common.constant.KafkaTopic;
import com.company.demo.common.response.exception.BusinessException;
import com.company.demo.giftcoupon.domain.repository.SseEmitterRepository;
import com.company.demo.giftcoupon.event.CouponIssuedEvent;
import com.company.demo.giftcoupon.handler.CouponEventHandler;
import com.company.demo.giftcoupon.handler.sevice.StatisticsService;
import com.company.demo.giftcoupon.mapper.dto.StatisticsDto;
import com.company.demo.giftcoupon.outbox.domain.event.CouponIssuedPayload;
import com.company.demo.giftcoupon.outbox.domain.event.DomainEventEnvelope;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper;

    @Override
    @KafkaListener(
            topics = KafkaTopic.COUPON_ISSUED,
            groupId = GroupType.STATS,
            containerFactory = "couponIssueKafkaListenerContainerFactory"
    )
    public void handle(DomainEventEnvelope<?> envelope) {
        processAndSendStats(envelope);
    }

    private void processAndSendStats(DomainEventEnvelope<?> envelope) {
        try {
            CouponIssuedPayload payload = objectMapper.convertValue(
                    envelope.payload(),
                    CouponIssuedPayload.class
            );

            log.info("[통계 핸들러]");
            Long promotionId = payload.promotionId();
            Long userId = payload.userId();

            StatisticsDto updatedStats = statisticsService.processIssuedEvent(userId, promotionId);
            sendStatsViaSse(updatedStats);

        } catch (Exception e) {
            log.error("통계 실패 - EventId: {}, Error: {}",
                    envelope.eventId(), e.getMessage(), e);
        }
    }

    private void sendStatsViaSse(StatisticsDto updatedStats) {
        Long publisherId = updatedStats.publisherId();
        SseEmitter emitter = sseEmitterRepository.findById(publisherId);

        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("coupon-stats-update")
                        .data(updatedStats)
                );
                log.info("실시간 통계 전송 성공 - PublisherId: {}, Data={}", publisherId, updatedStats);
            } catch (IOException e) {
                sseEmitterRepository.deleteById(publisherId);
                log.error("SSE 전송 실패, Emitter 제거: {}", publisherId, e);
            }
        } else {
            log.debug("No SSE emitter found for publisherId: {}", publisherId);
        }
    }
}
