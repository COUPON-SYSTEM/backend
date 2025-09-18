package com.company.demo.giftcoupon.sevice;

import com.company.demo.common.constant.EventType;
import com.company.demo.common.constant.Source;
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
        // 쿠폰 발급 시도
        CouponIssuanceResult result = this.issueCoupon(command);
        if (!result.isSuccess()) return;

        Coupon coupon = result.getCoupon();

        // 회원이 쿠폰을 발급받은 사실(주관심사)을 기록하면서
        // 동시에 이 사실을 담은 메시지(비괌심사)의 고유 ID를 넣으면 관심사 분리가 모호해짐
        CouponIssuedEvent event = new CouponIssuedEvent(
                null,
                command.memberId(),
                coupon.getId(),
                EventType.ISSUED_EVENT,
                LocalDateTime.now()
        );

        // 도메인 이벤트 → envelope로 감싸 발행
        DomainEventEnvelope<CouponIssuedPayload> envelope = event.toEnvelope(command.source());
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