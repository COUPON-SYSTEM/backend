package com.company.demo.giftcoupon.outbox.domain.repository;

import com.company.demo.giftcoupon.outbox.domain.entity.CouponIssuanceOutboxEvent;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public interface CustomCouponIssuanceOutboxRepository {
    @Transactional
    Optional<CouponIssuanceOutboxEvent> claimOneFailedForRetry(LocalDateTime now, Duration lockTtl);
}
