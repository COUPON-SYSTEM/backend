package com.company.demo.giftcoupon.outbox.domain.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.company.demo.giftcoupon.outbox.domain.entity.CouponIssuanceOutboxEvent;


import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CouponIssuanceOutboxRepository extends JpaRepository<CouponIssuanceOutboxEvent, Long> {

    @Query("""
        select o from CouponIssuanceOutboxEvent o
        where o.published = false
        order by o.createdAt asc
        """)
    List<CouponIssuanceOutboxEvent> pickUnpublished(Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update CouponIssuanceOutboxEvent o set o.published = true where o.id in :ids")
    int bulkMarkPublished(@Param("ids") List<Long> ids);

    @Modifying
    @Query("update CouponIssuanceOutboxEvent o set o.status='SENT', o.sentAt=CURRENT_TIMESTAMP, o.lockedUntil=null, o.error=null " +
            "where o.id=:id")
    int markSent(@Param("id") Long id);

    @Modifying
    @Query("update CouponIssuanceOutboxEvent o set o.status='FAILED', o.attempts=o.attempts+1, " +
            "o.nextRetryAt=:next, o.lockedUntil=null, o.error=:err " +
            "where o.id=:id")
    int backoff(@Param("id") Long id, @Param("next") LocalDateTime nextRetryAt, @Param("err") String error);
}
