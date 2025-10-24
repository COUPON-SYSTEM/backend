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
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponIssuedWriter implements ItemWriter<ProcessedCouponData> {

    private final CouponRepository couponRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final CouponExternalEventRecorder eventRecorder;

    @Override
    public void write(Chunk<? extends ProcessedCouponData> items) {
        log.info("writer 호출! chunkSize={}", items.size());
        for (ProcessedCouponData data : items) {
            try {
                log.info("[1] before save, userId={}", data.coupon().getUserId());
                couponRepository.save(data.coupon());               // ← 여기서 터지거나 막힐 가능성 높음
                log.info("[2] after save, couponId={}", data.coupon().getId());

                DomainEventEnvelope<CouponIssuedPayload> envelope =
                        data.event().toEnvelope(Source.COUPON_ISSUED, data.coupon().getId());
                log.info("[3] after envelope build: eventId={}", envelope.eventId());

                eventRecorder.record(envelope);
                log.info("[4] after outbox record");

                // 커밋 확정 후만 발행되게 하는 게 더 안전
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override public void afterCommit() {
                        applicationEventPublisher.publishEvent(envelope);
                        log.info("[5] AFTER_COMMIT publish done");
                    }
                });

                log.info("tx active? {}", TransactionSynchronizationManager.isActualTransactionActive());
            } catch (Exception e) {
                log.error("writer loop error: {}", e.toString(), e);
                throw e; // skip 정책이 있다면 여기서 스킵으로 넘어감
            }
        }
    }
}
