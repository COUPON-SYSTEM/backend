package com.company.demo.giftcoupon.sevice;

import com.company.demo.giftcoupon.domain.entity.Coupon;
import com.company.demo.giftcoupon.mapper.dto.request.CouponIssueRequest;
import com.company.demo.giftcoupon.mapper.dto.response.CouponIssueResponse;
import com.company.demo.giftcoupon.producer.CustomKafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.company.demo.giftcoupon.domain.repository.CouponRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponIssueService {
    private final CustomKafkaProducer customKafkaProducer;
    private final CouponRepository couponRepository;

    @Transactional
    public CouponIssueResponse issueCoupon(CouponIssueRequest request) {
        customKafkaProducer.sendMessage(ISSUE_TOPIC, request);
        Coupon coupon = Coupon.codeOnlyBuilder()
                .code("메롱")
                .build();
        couponRepository.save(coupon);
        return CouponIssueResponse.of(coupon);
    }
}