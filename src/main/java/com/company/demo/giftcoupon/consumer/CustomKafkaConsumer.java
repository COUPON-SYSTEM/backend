package com.company.demo.giftcoupon.consumer;

import com.company.demo.common.constant.KafkaTopic;
import com.company.demo.giftcoupon.mapper.dto.request.CouponIssueRequest;
import com.company.demo.giftcoupon.sevice.CouponIssueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomKafkaConsumer {

    private final CouponIssueService couponIssueService;

    @KafkaListener(topics = "test-topic", groupId = "consumer-group1")
    public void handleMessage(String message) {
        log.info("Received message: {}", message);
    }

    @KafkaListener(topics = KafkaTopic.COUPON_REQUEST)
    public void handleCouponRequest(CouponIssueRequest request) {
        couponIssueService.issueCoupon(request);
    }
}
