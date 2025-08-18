package com.company.demo.giftcoupon.sevice;

import com.company.demo.giftcoupon.domain.entity.Coupon;
import com.company.demo.giftcoupon.mapper.dto.request.CouponIssueRequestDto;
import com.company.demo.giftcoupon.mapper.dto.response.CouponIssueResponseDto;
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
    public CouponIssueResponseDto issueCoupon(CouponIssueRequestDto request) {
        Coupon coupon = Coupon.codeOnlyBuilder()
                .code("메롱")
                .build();
        couponRepository.save(coupon);
        log.info("쿠폰 DB에 저장 완료");
        return CouponIssueResponseDto.of(coupon);

        // 2. Kafka에 "발급 완료" 메시지 발행
        //CouponIssuedEvent event = new CouponIssuedEvent(userId, ...);
        // kafkaTemplate.send("coupon-issued-topic", event);
    }
}