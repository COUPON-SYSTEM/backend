package com.company.demo.giftcoupon.sevice;

import com.company.demo.giftcoupon.event.CouponIssuedEvent;
import com.company.demo.giftcoupon.handler.CouponEventHandler;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class NotificationListener implements CouponEventHandler {

    // TODO: 각각 핸들러를 분리해야할지, 동기 비동기 중 무엇으로 처리할지
    @Override
    public void handle(CouponIssuedEvent event) {
        notification(event);
    }

    public void notification(CouponIssuedEvent event){
        // TODO:
        // TODO: 이벤트 발급 시 FinalizeListener로 전송
    }

    public void pushSSE (CouponIssuedEvent event){
        // TODO:
        // TODO: 이벤트 발급 시 FinalizeListener로 전송
    }

    public void externalNotification (CouponIssuedEvent event){
        // TODO: 외부 알림 전송
        // TODO: 이벤트 발급 시 FinalizeListener로 전송
    }
}
