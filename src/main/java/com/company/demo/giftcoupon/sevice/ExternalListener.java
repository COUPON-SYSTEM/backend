package com.company.demo.giftcoupon.sevice;

import com.company.demo.giftcoupon.event.CouponIssuedEvent;
import com.company.demo.giftcoupon.handler.CouponEventHandler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ExternalListener implements CouponEventHandler {
    @Override
    @Async
    public void handle(CouponIssuedEvent event) {
        externalNotification(event);
    }

    public void externalNotification(CouponIssuedEvent event){
        // TODO: 외부 알림 전송
        // TODO: 이벤트 발급 시 FinalizeListener로 전송
    }
}
