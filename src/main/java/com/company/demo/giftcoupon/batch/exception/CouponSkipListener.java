package com.company.demo.giftcoupon.batch.exception;

import com.company.demo.giftcoupon.batch.CouponIssueInput;
import com.company.demo.giftcoupon.batch.ProcessedCouponData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.annotation.OnSkipInProcess;
import org.springframework.batch.core.annotation.OnSkipInRead;
import org.springframework.batch.core.annotation.OnSkipInWrite;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CouponSkipListener {

    @OnSkipInRead
    public void onSkipInRead(Throwable t) {
        log.warn("[SKIP][READ] {}", t.toString(), t);
    }

    @OnSkipInProcess
    public void onSkipInProcess(CouponIssueInput item, Throwable t) {
        log.warn("[SKIP][PROCESS] item={}, err={}", item, t.toString(), t);
    }

    @OnSkipInWrite
    public void onSkipInWrite(ProcessedCouponData item, Throwable t) {
        log.warn("[SKIP][WRITE] item={}, err={}", item, t.toString(), t);
    }
}
