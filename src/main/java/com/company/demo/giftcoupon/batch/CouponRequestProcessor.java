package com.company.demo.giftcoupon.batch;

import com.company.demo.giftcoupon.event.CouponRequestEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.batch.item.ItemProcessor;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponRequestProcessor implements ItemProcessor<List<String>, List<CouponRequestEvent>>{

    @Override
    public List<CouponRequestEvent> process(List<String> userIds) {
        return userIds.stream()
                .map(CouponRequestEvent::new)
                .toList();
    }
}
