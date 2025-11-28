package com.company.demo.giftcoupon.outbox.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.company.demo.giftcoupon.outbox.domain.entity.CouponIssuanceOutboxEvent;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CouponIssuanceOutboxRepository extends JpaRepository<CouponIssuanceOutboxEvent, Long>, CustomCouponIssuanceOutboxRepository {
    @Query("select e from CouponIssuanceOutboxEvent e where e.eventId = :eventId")
    Optional<CouponIssuanceOutboxEvent> findByEventId(@Param("eventId")String eventId);
}
