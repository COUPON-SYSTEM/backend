package com.company.demo.giftcoupon.controller;

import com.company.demo.giftcoupon.batch.queue.CouponRequestRedisQueue;
import com.company.demo.common.response.ApiResponse;
import com.company.demo.giftcoupon.mapper.dto.request.CouponIssueRequest;
import com.company.demo.giftcoupon.mapper.dto.response.MyCouponResponse;
import com.company.demo.giftcoupon.sevice.CouponIssueService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/coupons")
public class CouponController {

    private final CouponRequestRedisQueue couponRequestRedisQueue;
    private final CouponIssueService couponIssueService;

    @PostMapping("/request")
    public ApiResponse<String> requestCoupon(@RequestBody CouponIssueRequest request) {
        // "coupon:queue" 리스트에 userId를 넣되, 100명 이상이면 실패
        couponRequestRedisQueue.tryPush(String.valueOf(request.getUserId()), String.valueOf(request.getPromotionId()));
        return ApiResponse.success("신청 완료");
    }

    @GetMapping("/my")
    public ApiResponse<List<MyCouponResponse>> getMyCoupons(@RequestParam Long userId) {
        return ApiResponse.success(couponIssueService.getMyCoupons(userId));
    }
}
