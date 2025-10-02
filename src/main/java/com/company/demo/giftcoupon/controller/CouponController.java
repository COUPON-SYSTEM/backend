package com.company.demo.giftcoupon.controller;

import com.company.demo.common.response.ApiResponse;
import com.company.demo.common.response.error.ErrorCode;
import com.company.demo.giftcoupon.mapper.dto.request.CouponIssueRequest;
import com.company.demo.giftcoupon.queue.CouponRequestRedisQueue;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.company.demo.common.response.ApiResponse.error;
import static com.company.demo.common.response.ApiResponse.success;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/coupons")
public class CouponController {

    private final CouponRequestRedisQueue couponRequestRedisQueue;

    @PostMapping("/request")
    public ApiResponse<String> requestCoupon(@RequestBody CouponIssueRequest request) {
        // "coupon:queue" 리스트에 userId를 넣되, 100명 이상이면 실패
        couponRequestRedisQueue.tryPush(String.valueOf(request.getUserId()));
        return ApiResponse.success("신청 완료");
    }
}
