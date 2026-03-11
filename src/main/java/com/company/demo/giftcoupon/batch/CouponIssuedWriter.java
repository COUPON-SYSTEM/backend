package com.company.demo.giftcoupon.batch;

import com.company.demo.common.constant.Source;
import com.company.demo.common.response.error.ErrorCode;
import com.company.demo.giftcoupon.batch.exception.RetryInfraException;
import com.company.demo.giftcoupon.batch.exception.SkipDataException;
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
                couponRepository.save(data.coupon()); // DB save
                log.info("[2] after save, couponId={}", data.coupon().getId());

                DomainEventEnvelope<CouponIssuedPayload> envelope =
                        data.event().toEnvelope(
                                Source.COUPON_ISSUANCE_SERVICE,
                                data.coupon().getId(),
                                data.coupon().getPromotionId(),
                                data.coupon().getUserId()
                        );
                log.info("[3] after envelope build: eventId={}", envelope.eventId());

                eventRecorder.record(envelope);
                log.info("[4] after outbox record");

                // 커밋 이후에만 이벤트 발행
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        try {
                            applicationEventPublisher.publishEvent(envelope);
                            log.info("[5] AFTER_COMMIT publish done");
                        } catch (Exception e) {
                            // 커밋 이후 실패는 배치 retry/rollback로 해결 불가
                            log.error(
                                    "AFTER_COMMIT publish failed. code={}, msg={}",
                                    ErrorCode.NOTIFICATION_SEND_FAILED.getCode(),
                                    ErrorCode.NOTIFICATION_SEND_FAILED.getMessage(),
                                    e
                            );
                        }
                    }
                });

                log.info("tx active? {}", TransactionSynchronizationManager.isActualTransactionActive());

            }
            // =========================
            // DB 유니크/제약 위반
            // =========================
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                log.error("DB constraint violation (duplicate / unique)", e);
                throw new SkipDataException(ErrorCode.BUSINESS_RULE);
            }
            // =========================
            // DB 접근 오류
            // =========================
            catch (org.springframework.dao.DataAccessException e) {
                log.error("DB access error", e);
                throw new RetryInfraException(ErrorCode.DB_CONNECTION_FAILED);
            }
            // =========================
            // 이미 분류된 예외는 그대로 전달
            // =========================
            catch (SkipDataException | RetryInfraException e) {
                log.error("classified batch exception: {}", e.getErrorCode(), e);
                throw e;
            }
            // =========================
            // 알 수 없는 예외는 그대로 fail
            // =========================
            catch (Exception e) {
                log.error("writer loop unknown error", e);
                throw e;
            }
        }
    }
}
