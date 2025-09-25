package com.company.demo.giftcoupon.handler;

import com.company.demo.giftcoupon.event.CouponIssuedEvent;
import com.company.demo.giftcoupon.handler.CouponEventHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.amqp.core.AmqpTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExternalListener implements CouponEventHandler { // 외부시스템에 데이터를 동기화

    private final AmqpTemplate amqpTemplate;

    @Override
    @Async
    public void handle(CouponIssuedEvent event) {
        externalNotification(event);
    }

    public void externalNotification(CouponIssuedEvent event){
        try {
            // 메시지 큐로 이벤트 데이터 발행
            // 'exchange'는 메시지를 라우팅하는 교환소, 'routing-key'는 라우팅 키
            amqpTemplate.convertAndSend("external-system-exchange", "coupon.issued", event);

            log.info("메시지 큐에 이벤트 발행 완료 - CouponId: {}", event.getCouponId());
        } catch (Exception e) {
            log.error("메시지 큐 발행 실패 - CouponId: {}", event.getCouponId(), e.getMessage(), e);
        }
    }
}
