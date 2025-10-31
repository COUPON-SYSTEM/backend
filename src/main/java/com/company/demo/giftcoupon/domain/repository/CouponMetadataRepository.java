package com.company.demo.giftcoupon.domain.repository;

import com.company.demo.giftcoupon.domain.entity.CouponMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CouponMetadataRepository extends JpaRepository<CouponMetadata, Long> {
    @Query("select c from CouponMetadata c where c.couponId =: couponId")
    Optional<CouponMetadata> findByCouponId(@Param("couponId") Long couponId);
}
