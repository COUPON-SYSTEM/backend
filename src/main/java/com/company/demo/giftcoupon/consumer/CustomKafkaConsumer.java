package com.company.demo.giftcoupon.consumer;

import com.company.demo.common.constant.KafkaTopic;
import com.company.demo.giftcoupon.mapper.dto.request.CouponIssuanceRequestDto;
import com.company.demo.giftcoupon.sevice.CouponIssueService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.company.demo.giftcoupon.mapper.dto.request.TryIssueCouponCommand;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomKafkaConsumer {

    private final CouponIssueService couponIssueService;

    @KafkaListener(topics = "test-topic", groupId = "consumer-group1")
    public void handleMessage(String message) {
        log.info("Received message: {}", message);
    }

    @KafkaListener(topics = KafkaTopic.COUPON_ISSUANCE)
    public void handleIssuanceMessage(String messageJson) throws JsonProcessingException {
        // 1. 메시지 역직렬화 (예: ObjectMapper 사용)
        CouponIssuanceRequestDto dto = new ObjectMapper().readValue(messageJson, CouponIssuanceRequestDto.class);

        // 2. DTO -> Command 변환 후 서비스 호출
        couponIssueService.tryToIssueCoupon(TryIssueCouponCommand.from(dto));
    }
}
