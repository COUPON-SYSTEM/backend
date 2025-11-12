package com.company.demo.giftcoupon.outbox.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.company.demo.giftcoupon.outbox.domain.entity.CouponIssuanceOutboxEvent;

public interface CouponIssuanceOutboxRepository extends JpaRepository<CouponIssuanceOutboxEvent, Long>, CustomCouponIssuanceOutboxRepository {
}
