package com.company.demo.giftcoupon.consumer;

import com.company.demo.common.constant.KafkaTopic;
import com.company.demo.giftcoupon.mapper.dto.request.CouponIssueRequest;
import com.company.demo.giftcoupon.sevice.CouponIssueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomKafkaConsumer {

    private final CouponIssueService couponIssueService;

    @KafkaListener(topics = "test-topic", groupId = "consumer-group1")
    public void handleMessage(String message) {
        log.info("Received message: {}", message);
    }

    @Transactional
    public void tryToIssuanceCoupon(final TimeAttackCouponIssuance issuance) {

        // 1. 선착순 쿠폰 발급 시도
        CouponIssuanceResult result = timeAttackCouponIssuer.tryIssuance(issuance);

        // 2. Spring Event 발행
        if (result.isSuccess()) {
            DomainEventEnvelop envelop = result.toEvent();
            applicationEventPublisher.publish(envelop);
        }
    }

//    @KafkaListener(topics = KafkaTopic.COUPON_ISSUANCE)
//    public void handleCouponRequest(CouponIssueRequest request) {
//        couponIssueService.issueCoupon(request);
//    }
}
