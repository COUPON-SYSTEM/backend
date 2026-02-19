package com.company.demo.giftcoupon.sevice;

import com.company.demo.common.constant.EventType;
import com.company.demo.giftcoupon.domain.entity.Coupon;
import com.company.demo.giftcoupon.domain.entity.CouponMetadata;
import com.company.demo.giftcoupon.domain.repository.CouponMetadataRepository;
import com.company.demo.giftcoupon.domain.repository.CouponRepository;
import com.company.demo.giftcoupon.domain.repository.UserRepository;
import com.company.demo.giftcoupon.mapper.dto.response.MyCouponResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class CouponIssueService {

    private final CouponRepository couponRepository;
    private final CouponMetadataRepository couponMetadataRepository;

    public Coupon issueCoupon(final String userId, final String couponId) {
        return Coupon.builder()
                .userId(Long.valueOf(userId))
                .promotionId(Long.valueOf(couponId))
                .code("안녕하세요")
                .eventType(EventType.ISSUED_EVENT)
                .build();
    }

    @Transactional(readOnly = true)
    public List<MyCouponResponse> getMyCoupons(Long userId) {
        List<Coupon> coupons = couponRepository.findByUserId(userId);

        List<Long> promotionIds = coupons.stream()
                .map(Coupon::getPromotionId)
                .distinct()
                .toList();

        Map<Long, CouponMetadata> metadataMap = couponMetadataRepository.findByPromotionIdIn(promotionIds)
                .stream()
                .collect(Collectors.toMap(CouponMetadata::getPromotionId, Function.identity()));

        return coupons.stream()
                .map(coupon -> MyCouponResponse.of(coupon, metadataMap.get(coupon.getPromotionId())))
                .toList();
    }
}