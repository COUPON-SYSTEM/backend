package com.company.demo.giftcoupon.outbox.listener;

import com.company.demo.giftcoupon.outbox.domain.event.CouponIssuedEvent;
import com.company.demo.giftcoupon.outbox.domain.event.CouponIssuedPayload;
import com.company.demo.giftcoupon.outbox.domain.event.DomainEventEnvelope;
import com.company.demo.giftcoupon.producer.CustomKafkaProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class CouponIssuedEventMessageListener{
    private final CustomKafkaProducer customKafkaProducer;

    // 2. 카프카에 메시지 전송 (TransactionPhase.AFTER_COMMIT)
    // @Async(EVENT_ASYNC_TASK_EXECUTOR)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendMessageHandler(DomainEventEnvelope<CouponIssuedPayload> envelope) {
        customKafkaProducer.sendIssuedMessage(envelope);
    }
}
