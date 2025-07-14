package com.company.demo.giftcoupon.controller;

import com.company.demo.giftcoupon.mapper.dto.request.CouponIssueRequest;
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

    private final RedisTemplate<String, String> redisTemplate;

    private static final String COUPON_QUEUE_KEY = "coupon:queue";
    private static final String COUPON_ISSUED_SET_KEY = "coupon:issued";

    @PostMapping("/issue")
    public ResponseEntity<String> issueCoupon(@RequestBody CouponIssueRequest request) {
        String userId = request.getUserId();

        // 중복 발급 방지 - 이미 발급한 유저인지 Redis Set에서 확인
        Boolean alreadyIssued = redisTemplate.opsForSet().isMember(COUPON_ISSUED_SET_KEY, userId);
        if (Boolean.TRUE.equals(alreadyIssued)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("이미 쿠폰을 발급받은 사용자입니다.");
        }

        // Redis Set에 추가 (중복 발급 방지용)
        redisTemplate.opsForSet().add(COUPON_ISSUED_SET_KEY, userId);

        // 큐에 발급 요청 저장
        redisTemplate.opsForList().leftPush(COUPON_QUEUE_KEY, userId);

        return ResponseEntity.ok("쿠폰 발급 요청이 접수되었습니다.");
    }
}
