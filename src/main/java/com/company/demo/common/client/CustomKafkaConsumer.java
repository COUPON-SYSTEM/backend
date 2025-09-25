package com.company.demo.common.client;

import com.company.demo.common.constant.KafkaTopic;
import com.company.demo.giftcoupon.event.CouponIssuedEvent;
import com.company.demo.giftcoupon.handler.CouponIssueListener;
import com.company.demo.giftcoupon.outbox.domain.event.DomainEventEnvelope;
import com.company.demo.giftcoupon.event.CouponIssueEvent;
import com.company.demo.giftcoupon.event.CouponIssuePayload;
import com.company.demo.giftcoupon.mapper.dto.request.CouponIssueRequest;
import com.company.demo.giftcoupon.outbox.domain.event.DomainEventEnvelope;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.company.demo.giftcoupon.outbox.domain.entity.TryIssueCouponCommand;
import com.company.demo.giftcoupon.sevice.CouponIssueService;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomKafkaConsumer {

    private final CouponIssueService couponIssueService;
    private final CouponIssueListener couponIssueListener;

    @KafkaListener(topics = KafkaTopic.TEST_TOPIC, groupId = "consumer-group1")
    public void handleMessage(String message) {
        log.info("Received message: {}", message);
    }

    @KafkaListener(topics = KafkaTopic.COUPON_ISSUE)
    public void handleIssuanceMessage(DomainEventEnvelope<CouponIssuePayload> envelope) {
        couponIssueService.tryToIssueCoupon(TryIssueCouponCommand.from(envelope.payload().memberId(), envelope.source()));
    }

    @KafkaListener(topics = KafkaTopic.COUPON_ISSUED)
    public void handleCouponIssued(CouponIssuedEvent event) {
        couponIssueListener.handle(event);
    }
}