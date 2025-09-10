package com.company.demo.giftcoupon.controller;

import com.company.demo.giftcoupon.config.queue.CouponRequestRedisQueue;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/coupons")
public class CouponController {

    private final CouponRequestRedisQueue couponRequestRedisQueue;

    @PostMapping("/request")
    public ResponseEntity<String> requestCoupon(@RequestParam(value = "userId") String userId) {
        // "coupon:queue" 리스트에 userId를 넣되, 100명 이상이면 실패
        boolean success = couponRequestRedisQueue.tryPush(userId);
        return success ? ok("신청 완료") : status(429).body("마감됨");
    }
}
