package com.company.demo.giftcoupon.handler;

import com.company.demo.giftcoupon.event.CouponIssuedEvent;
import org.springframework.stereotype.Component;

public interface CouponEventHandler {
    void handle(CouponIssuedEvent event);
}
