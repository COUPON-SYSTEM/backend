package com.company.demo.giftcoupon.controller;

import com.company.demo.giftcoupon.mapper.dto.request.CouponIssueRequest;
import com.company.demo.giftcoupon.queue.GiftRequestRedisQueue;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/coupons")
public class CouponIssueController {

    private final GiftRequestRedisQueue giftRequestRedisQueue;

    private static final String COUPON_QUEUE_KEY = "coupon:queue";
    private static final String COUPON_ISSUED_SET_KEY = "coupon:issued";

    @PostMapping("/request")
    public ResponseEntity<String> issueCoupon(@RequestBody CouponIssueRequest request) {
        String userId = request.getUserId();
        giftRequestRedisQueue.popFromQueueAndSendToKafka();
        return ResponseEntity.ok("쿠폰 발급 요청이 접수되었습니다.");
    }
}
