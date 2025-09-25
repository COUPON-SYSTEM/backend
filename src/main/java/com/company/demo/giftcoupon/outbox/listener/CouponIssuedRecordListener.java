package com.company.demo.giftcoupon.outbox.listener;

import com.company.demo.giftcoupon.outbox.recorder.CouponExternalEventRecorder;
import com.company.demo.giftcoupon.outbox.domain.event.CouponIssuedPayload;
import com.company.demo.giftcoupon.outbox.domain.event.DomainEventEnvelope;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class CouponIssuedRecordListener {
    private final CouponExternalEventRecorder eventRecorder;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void recordEvent(DomainEventEnvelope<CouponIssuedPayload> envelope) {
        eventRecorder.record(envelope); // payload를 JSON 직렬화해 Outbox에 insert
    }
}
