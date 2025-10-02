package com.company.demo.giftcoupon.sevice;

import com.company.demo.giftcoupon.domain.entity.Coupon;
import com.company.demo.giftcoupon.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class CouponIssueService {
    private final UserRepository userRepository;

    public Coupon issueCoupon(final String userId) {
//        if(!userRepository.existsById(Long.valueOf(userId))){
//            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
//        }

        return Coupon.builder()
                .userId(Long.valueOf(userId))
                .code("안녕하세요")
                .build();
    }
}