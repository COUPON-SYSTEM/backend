package com.company.demo.giftcoupon.batch;

import com.company.demo.giftcoupon.event.CouponIssuanceEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponRequestProcessor implements ItemProcessor<String, CouponIssuanceEvent> {

    @Override
    public CouponIssuanceEvent process(String userId) {
        return new CouponIssuanceEvent(userId);
    }
}
