package com.company.demo.giftcoupon.sevice;

import com.company.demo.common.constant.EventType;
import com.company.demo.giftcoupon.domain.entity.Coupon;
import com.company.demo.giftcoupon.outbox.domain.entity.TryIssueCouponCommand;
import com.company.demo.giftcoupon.outbox.domain.event.CouponIssuedEvent;
import com.company.demo.giftcoupon.outbox.domain.event.CouponIssuedPayload;
import com.company.demo.giftcoupon.outbox.domain.event.DomainEventEnvelope;
import com.company.demo.giftcoupon.outbox.domain.result.CouponIssuanceResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.company.demo.giftcoupon.domain.repository.CouponRepository;

import java.time.LocalDateTime;


@Slf4j
@Service
@RequiredArgsConstructor
public class CouponIssueService {
    private final CouponRepository couponRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public void tryToIssueCoupon(final TryIssueCouponCommand command) {
        // 1) 쿠폰 발급 시도
        CouponIssuanceResult result = this.issueCoupon(command);
        if (!result.isSuccess()) return;

        Coupon coupon = result.getCoupon();

        // 2) 도메인 이벤트 → envelope로 감싸 발행
        CouponIssuedEvent event = new CouponIssuedEvent(
                command.memberId(),
                coupon.getId(),
                EventType.ISSUED_EVENT,
                LocalDateTime.now()
        );

        // source는 서비스 식별자나 컨텍스트 값(예: command.source())로 지정
        DomainEventEnvelope<CouponIssuedPayload> envelope = event.toEnvelope("giftcoupon-service");
        applicationEventPublisher.publishEvent(envelope);
        log.info("쿠폰 발급 이벤트(envelope) 발행 완료. memberId={}, couponId={}", command.memberId(), coupon.getId());
    }

    private CouponIssuanceResult issueCoupon(TryIssueCouponCommand command) {
        Coupon coupon = Coupon.builder()
                .memberId(command.memberId())
                .code(command.source())
                .build();
        couponRepository.save(coupon);
        log.info("쿠폰 DB에 저장 완료");
        return CouponIssuanceResult.from(coupon);
    }
}