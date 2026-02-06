package com.company.demo.giftcoupon.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "coupon_metadata")
public class CouponMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "promotion_id", unique = true, nullable = false)
    private Long promotionId;

    @Column(name = "publisher_id", nullable = false)
    private Long publisherId;

    @Column(name = "name", nullable = false)
    private String name;

    // 쿠폰 매출
    @Column(name = "value")
    private Long value;

    // 총 발행 가능 수량
    @Column(name = "total_capacity")
    private Long totalCapacity;

    @Column(name = "issued_count")
    private Long issuedCount = 0L;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

}
