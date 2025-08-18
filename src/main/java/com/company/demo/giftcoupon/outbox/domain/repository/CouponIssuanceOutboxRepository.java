package com.company.demo.giftcoupon.outbox.domain.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.company.demo.giftcoupon.outbox.domain.entity.CouponIssuanceOutbox;


import java.util.List;

public interface CouponIssuanceOutboxRepository extends JpaRepository<CouponIssuanceOutbox, Long> {

    @Query("""
        select o from CouponIssuanceOutbox o
        where o.published = false
        order by o.createdAt asc
        """)
    List<CouponIssuanceOutbox> pickUnpublished(Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update CouponIssuanceOutbox o set o.published = true where o.id in :ids")
    int bulkMarkPublished(@Param("ids") List<Long> ids);
}
