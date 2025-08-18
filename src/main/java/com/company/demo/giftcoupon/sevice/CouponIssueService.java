package com.company.demo.giftcoupon.sevice;

import com.company.demo.giftcoupon.domain.entity.Coupon;
import com.company.demo.giftcoupon.mapper.dto.request.CouponIssuanceRequestDto;
import com.company.demo.giftcoupon.mapper.dto.response.CouponIssueResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.company.demo.giftcoupon.domain.repository.CouponRepository;


@Slf4j
@Service
@RequiredArgsConstructor
public class CouponIssueService {
    private final CouponRepository couponRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public void tryToIssueCoupon(final CouponIssuanceRequestDto requestDto) {

        // 1. 선착순 쿠폰 발급 시도(발급 여부 검증)
        // CouponIssuanceResult result = timeAttackCouponIssuer.tryIssuance(requestDto);
        CouponIssueResponseDto result = this.issueCoupon(requestDto);
        ApplicationEvent
        // 2. Spring Event 발행
        if (result.isSuccess()) {
            DomainEventEnvelop envelop = result.toEvent();
            applicationEventPublisher.publishEvent(envelop);
        }
    }

    private CouponIssueResponseDto issueCoupon(CouponIssuanceRequestDto request) {
        Coupon coupon = Coupon.codeOnlyBuilder()
                .code("메롱")
                .build();
        couponRepository.save(coupon);
        log.info("쿠폰 DB에 저장 완료");
        return CouponIssueResponseDto.of(coupon);
    }
}