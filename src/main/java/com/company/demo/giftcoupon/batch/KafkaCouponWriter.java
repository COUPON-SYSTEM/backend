package com.company.demo.giftcoupon.batch;

import com.company.demo.common.constant.Source;
import com.company.demo.giftcoupon.domain.repository.CouponRepository;
import com.company.demo.giftcoupon.outbox.domain.event.CouponIssuedPayload;
import com.company.demo.giftcoupon.outbox.domain.event.DomainEventEnvelope;
import com.company.demo.giftcoupon.outbox.recorder.CouponExternalEventRecorder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaCouponWriter implements ItemWriter<ProcessedCouponData> {

    private final CouponRepository couponRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final CouponExternalEventRecorder eventRecorder;

    @Override
    public void write(Chunk<? extends ProcessedCouponData> items) {
        log.info("writer 호출! chunkSize={}", items.size());
        for (ProcessedCouponData data : items) {
            // 1) 쿠폰 저장 -> couponId 확보
            couponRepository.save(data.coupon());
            log.info("쿠폰 ID {} 저장 완료.", data.coupon().getId());

            // 2) 엔벨로프 "최종" 구성 (couponId 포함)
            DomainEventEnvelope<CouponIssuedPayload> envelope =
                    data.event().toEnvelope(Source.COUPON_ISSUED, data.coupon().getId());


            eventRecorder.record(envelope);
            applicationEventPublisher.publishEvent(envelope);
            log.info("tx active? {}", TransactionSynchronizationManager.isActualTransactionActive());
            log.info("쿠폰 발급 이벤트(envelope) 발행 완료. userId={}, couponId={}", data.coupon().getUserId(), data.coupon().getId());
        }
    }
}
