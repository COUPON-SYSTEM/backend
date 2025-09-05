package com.company.demo.giftcoupon.sevice;

import com.company.demo.giftcoupon.domain.entity.Coupon;
import com.company.demo.giftcoupon.event.CouponIssuedEvent;
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
public class CouponIssueListener {
    private final CustomKafkaProducer customKafkaProducer;
    private final CouponRepository couponRepository;
    private final HistoryListener historyListener;
    private final NotificationListener notificationListener;

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

    // TODO: Transactional 단위를 어떻게 할지
    public void notificationCoupon(CouponIssuedEvent event) {
        historyListener.issuedHistorySave(event);
        notificationListener.notification(event);
    }
}