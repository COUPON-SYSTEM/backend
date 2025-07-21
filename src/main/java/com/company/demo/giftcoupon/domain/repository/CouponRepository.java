package com.company.demo.giftcoupon.domain.repository;

import com.company.demo.giftcoupon.domain.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

}
