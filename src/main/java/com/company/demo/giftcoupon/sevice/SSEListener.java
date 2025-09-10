package com.company.demo.giftcoupon.sevice;

import com.company.demo.giftcoupon.event.CouponIssuedEvent;
import com.company.demo.giftcoupon.handler.CouponEventHandler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class SSEListener implements CouponEventHandler {
    @Override
    @Async
    public void handle(CouponIssuedEvent event) {
        pushSSE(event);
    }

    public void pushSSE (CouponIssuedEvent event){
        // TODO:
        // TODO: 이벤트 발급 시 FinalizeListener로 전송
    }
}
