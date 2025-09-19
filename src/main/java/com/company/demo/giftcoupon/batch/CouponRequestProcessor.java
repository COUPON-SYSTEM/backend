package com.company.demo.giftcoupon.batch;

import com.company.demo.common.constant.EventType;
import com.company.demo.giftcoupon.event.CouponIssueEvent;
import com.company.demo.giftcoupon.outbox.domain.event.CouponIssuedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.batch.item.ItemProcessor;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponRequestProcessor implements ItemProcessor<String, CouponIssueEvent> {

    @Override
    public CouponIssueEvent process(String memberId) {
        return new CouponIssueEvent(
                null,
                memberId,
                EventType.ISSUE_EVENT,
                LocalDateTime.now());
    }
}
