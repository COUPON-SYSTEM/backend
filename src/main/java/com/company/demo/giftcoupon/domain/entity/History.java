package com.company.demo.giftcoupon.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id", nullable = false)
    private Long id;

    @Column(name = "promotion_id", nullable = false)
    private Long promotionId;

    @Column(name = "coupon_id", nullable = false)
    private Long couponId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "issuedAt", nullable = true)
    private LocalDateTime issuedAt;

    public History(Long promotionId, Long couponId, Long userId, LocalDateTime issuedAt) {
        this.promotionId = promotionId;
        this.couponId = couponId;
        this.userId = userId;
        this.issuedAt = issuedAt;
    }
}
