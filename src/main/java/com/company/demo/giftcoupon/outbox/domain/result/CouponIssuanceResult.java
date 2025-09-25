package com.company.demo.giftcoupon.outbox.domain.result;

import com.company.demo.giftcoupon.domain.entity.Coupon;
import com.company.demo.giftcoupon.outbox.domain.event.CouponIssuedEvent;

import com.company.demo.giftcoupon.outbox.domain.event.DomainEventEnvelope;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 쿠폰 발급 처리 결과
 * - 성공 여부와 쿠폰 정보, 실패 사유 등을 포함
 */
@Getter
@Builder
@RequiredArgsConstructor
public class CouponIssuanceResult {

    private final boolean isSuccess;
    private final Coupon coupon;     // 성공 시 발급된 쿠폰 엔티티
    private final String failReason; // 실패 시 사유 메시지

    /** 성공 팩토리 */
    public static CouponIssuanceResult success(Coupon coupon) {
        return new CouponIssuanceResult(true, coupon, null);
    }

    /** 실패 팩토리 */
    public static CouponIssuanceResult fail(String reason) {
        return new CouponIssuanceResult(false, null, reason);
    }

    /** 이벤트 변환 */
    public static CouponIssuanceResult from(Coupon coupon) {
        return success(coupon);
    }

    public boolean isSuccess() {return isSuccess;}
}

