package com.company.demo.giftcoupon.outbox.listener;

import com.company.demo.giftcoupon.outbox.domain.event.CouponIssuedEvent;
import com.company.demo.giftcoupon.outbox.domain.event.CouponIssuedPayload;
import com.company.demo.giftcoupon.outbox.domain.event.DomainEventEnvelope;
import com.company.demo.giftcoupon.producer.CustomKafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponIssuedEventMessageListener{
    private final CustomKafkaProducer customKafkaProducer;

    // 2. 카프카에 메시지 전송 (TransactionPhase.AFTER_COMMIT)
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendMessageHandler(DomainEventEnvelope<?> envelope) {
        log.info("===== 이벤트 리스너 시작 =====");
        log.info("현재 트랜잭션 활성화 상태: {}", TransactionSynchronizationManager.isActualTransactionActive());
        log.info("현재 트랜잭션 동기화 상태: {}", TransactionSynchronizationManager.isSynchronizationActive());
        customKafkaProducer.sendIssuedMessage((DomainEventEnvelope<CouponIssuedPayload>) envelope);
        log.info("카프카에 메세지 전송 완료!");
    }
}
