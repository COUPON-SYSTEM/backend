package com.company.demo.giftcoupon.sevice;

import com.company.demo.giftcoupon.domain.entity.Coupon;
import com.company.demo.giftcoupon.mapper.dto.request.CouponIssueRequest;
import com.company.demo.giftcoupon.producer.CustomKafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponIssueService {
    private final CustomKafkaProducer customKafkaProducer;
    private final CouponRespository couponRespository;

    @Transactional
    public CouponIssueResponse issueCoupon(CouponIssueRequest request) {
        customKafkaProducer.sendMessage(ISSUE_TOPIC, request);
        Coupon coupon = Coupon.codeOnlyBuilder()
                .code("메롱")
                .build();
        couponRespository.save(coupon);
        return CouponIssueResponse.of(coupon);
    }
}