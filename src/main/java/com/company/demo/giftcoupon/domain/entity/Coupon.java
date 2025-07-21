package com.company.demo.giftcoupon.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "coupons")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id", nullable = false)
    private Long id; // 쿠폰 아이디

    @Column(name = "event_id", nullable = true)
    private Long eventId;

    @Column(name = "coupon_code", nullable = true)
    private String code;

    @Column(name = "coupon_used_at", nullable = true)
    private LocalDateTime usedAt;

    @Builder
    public Coupon(Long eventId, String code, LocalDateTime usedAt) {
        this.eventId = eventId;
        this.code = code;
        this.usedAt = usedAt;
    }

    @Builder(builderMethodName = "codeOnlyBuilder") // 전용 메서드
    private Coupon(String code) {
        this.code = code;
    }


//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private Status status;
}
