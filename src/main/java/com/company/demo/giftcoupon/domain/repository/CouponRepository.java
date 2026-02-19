package com.company.demo.giftcoupon.domain.repository;

import com.company.demo.giftcoupon.domain.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    long countIssuedByEventType(String eventType);

    List<Coupon> findByUserId(Long userId);
}
