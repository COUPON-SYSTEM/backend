package com.company.demo.giftcoupon.batch;

import com.company.demo.common.constant.Source;
import com.company.demo.giftcoupon.domain.repository.CouponRepository;
import com.company.demo.giftcoupon.outbox.domain.event.CouponIssuedPayload;
import com.company.demo.giftcoupon.outbox.domain.event.DomainEventEnvelope;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaCouponWriter implements ItemWriter<ProcessedCouponData> {

    private final CouponRepository couponRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void write(Chunk<? extends ProcessedCouponData> items) {
        for (ProcessedCouponData data : items) {
            // 1) 쿠폰 저장 -> couponId 확보
            couponRepository.save(data.coupon());

            // 2) 엔벨로프 "최종" 구성 (couponId 포함)
            DomainEventEnvelope<CouponIssuedPayload> envelope =
                    data.event().toEnvelope(Source.COUPON_ISSUED, data.coupon().getId());

            applicationEventPublisher.publishEvent(envelope);
            log.info("쿠폰 발급 이벤트(envelope) 발행 완료. memberId={}, couponId={}", data.coupon().getMemberId(), data.coupon().getId());
        }
    }
}
