package com.company.demo.giftcoupon.outbox.domain.repository;

import com.company.demo.giftcoupon.outbox.domain.entity.CouponIssuanceOutboxEvent;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
class CouponIssuanceOutboxRepositoryImpl implements CustomCouponIssuanceOutboxRepository {

    private final EntityManager em;

    @Transactional
    @Override
    public Optional<CouponIssuanceOutboxEvent> claimOneUnpublishedForRetry(LocalDateTime threshold) {
        @SuppressWarnings("unchecked")
        List<Long> ids = em.createNativeQuery("""
            SELECT outbox_id
              FROM coupon_issuance_outbox_event
             WHERE is_published = false
               AND created_at <= :threshold
             ORDER BY created_at
             LIMIT 1
             FOR UPDATE SKIP LOCKED
        """)
                .setParameter("threshold", Timestamp.valueOf(threshold))
                .getResultList();

        if (ids.isEmpty()) return Optional.empty();
        return Optional.ofNullable(em.find(CouponIssuanceOutboxEvent.class, ids.get(0)));
    }
}