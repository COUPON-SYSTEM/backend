package com.company.demo.giftcoupon.handler;

import com.company.demo.giftcoupon.domain.entity.Coupon;
import com.company.demo.giftcoupon.event.CouponIssuedEvent;
import com.company.demo.giftcoupon.mapper.dto.request.CouponIssueRequest;
import com.company.demo.giftcoupon.mapper.dto.response.CouponIssueResponse;
import com.company.demo.common.client.CustomKafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.company.demo.giftcoupon.domain.repository.CouponRepository;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class CouponIssueListener {
    private final CustomKafkaProducer customKafkaProducer;
    private final CouponRepository couponRepository;
    private final List<CouponEventHandler> couponEventHandlers;

    @Transactional
    public CouponIssueResponse issueCoupon(CouponIssueRequest request) {
        Coupon coupon = Coupon.codeOnlyBuilder()
                .code("메롱")
                .build();
        couponRepository.save(coupon);
        log.info("쿠폰 DB에 저장 완료");
        return CouponIssueResponse.of(coupon);

        // 2. Kafka에 "발급 완료" 메시지 발행
        //CouponIssuedEvent event = new CouponIssuedEvent(userId, ...);
        // kafkaTemplate.send("coupon-issued-topic", event);
    }

    public void handle(CouponIssuedEvent event) {
        log.info("쿠폰 이벤트 핸들러 처리 시작 - CouponId: {}", event.getCouponId());

        for (CouponEventHandler handler : couponEventHandlers) {
            try {
                long startTime = System.currentTimeMillis();
                handler.handle(event);
                log.info("핸들러 처리 완료 - Type: {}, 소요시간: {}ms",
                        handler.getClass().getSimpleName(),
                        System.currentTimeMillis() - startTime);
            } catch (Exception e) {
                log.error("핸들러 처리 실패 - Type: {}, Error: {}",
                        handler.getClass().getSimpleName(), e.getMessage(), e);
            }
        }

        log.info("모든 쿠폰 이벤트 핸들러 처리 완료 - CouponId: {}", event.getCouponId());
    }
}