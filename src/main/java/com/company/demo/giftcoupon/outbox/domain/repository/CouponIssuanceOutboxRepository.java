package com.company.demo.giftcoupon.outbox.domain.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.company.demo.giftcoupon.outbox.domain.entity.CouponIssuanceOutboxEvent;


import java.util.List;

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
}
