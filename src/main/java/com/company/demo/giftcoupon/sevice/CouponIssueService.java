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

        // 1. 선착순 쿠폰 발급 시도(발급 여부 검증)
        CouponIssuanceResult result = this.issueCoupon(command);

        // 2) 성공 시 이벤트 → Envelope → publish
        if (result.isSuccess()) {
            // 이벤트 생성 (command + result 사용)
            CouponIssuedEvent event = new CouponIssuedEvent(
                    command.memberId(),
                    result.getCoupon().getId(),
                    EventType.ISSUED_EVENT,
                    LocalDateTime.now()
            );

            DomainEventEnvelope<CouponIssuedPayload> envelope = event.envelop();
            applicationEventPublisher.publishEvent(envelope);
        }
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