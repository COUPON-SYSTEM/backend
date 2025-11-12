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
    public Optional<CouponIssuanceOutboxEvent> claimOneFailedForRetry(LocalDateTime now, Duration lockTtl) {
        //        // 1) 재시도 대상 id 한 건 선점 (FAILED or IN_PROGRESS 타임아웃)
        @SuppressWarnings("unchecked")
        List<Long> ids = em.createNativeQuery("""
            SELECT id
            FROM coupon_outbox
            WHERE (
                    status = 'FAILED'
                 OR (status = 'IN_PROGRESS' AND (locked_until IS NULL OR locked_until <= :now))
                  )
              AND (next_retry_at IS NULL OR next_retry_at <= :now)
            ORDER BY created_at
            LIMIT 1
            FOR UPDATE SKIP LOCKED
        """).setParameter("now", Timestamp.valueOf(now))
                .getResultList();

        if (ids.isEmpty()) return Optional.empty();

        Long id = ids.get(0);

        // 2) 다시 시도하므로 IN_PROGRESS로 전환 + 락 부여
        em.createNativeQuery("""
            UPDATE coupon_outbox
               SET status='IN_PROGRESS', locked_until=:locked
             WHERE id=:id
        """).setParameter("locked", Timestamp.valueOf(now.plus(lockTtl)))
                .setParameter("id", id)
                .executeUpdate();

        return Optional.of(em.find(CouponIssuanceOutboxEvent.class, id));
    }
}