package com.company.demo.giftcoupon.handler;

import com.company.demo.giftcoupon.event.CouponIssuedEvent;

// TODO: 이벤트 처리를 위한 추상 인터페이스
public interface CouponEventHandler {
    void handle(CouponIssuedEvent event);
}
