package com.company.demo.giftcoupon.batch;

import com.company.demo.giftcoupon.event.CouponRequestEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponRequestProcessor implements ItemProcessor<String, CouponRequestEvent> {

    @Override
    public CouponRequestEvent process(String userId) {
        return new CouponRequestEvent(userId);
    }
}
