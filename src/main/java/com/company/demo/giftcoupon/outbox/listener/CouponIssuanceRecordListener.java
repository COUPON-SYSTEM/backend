package com.company.demo.giftcoupon.outbox.listener;

import com.company.demo.giftcoupon.outbox.CouponExternalEventRecorder;
import com.company.demo.giftcoupon.outbox.domain.event.DomainEventEnvelope;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class CouponIssuanceRecordListener {
    private final CouponExternalEventRecorder eventRecorder;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void recordEvent(DomainEventEnvelope<?> env) {
        eventRecorder.record(env); // payload를 JSON 직렬화해 Outbox에 insert
    }
}
