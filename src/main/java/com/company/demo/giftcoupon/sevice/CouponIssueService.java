package com.company.demo.giftcoupon.sevice;

import com.company.demo.common.constant.EventType;
import com.company.demo.common.constant.Source;
import com.company.demo.common.response.error.ErrorCode;
import com.company.demo.common.response.exception.BusinessException;
import com.company.demo.giftcoupon.domain.entity.Coupon;
import com.company.demo.giftcoupon.domain.entity.User;
import com.company.demo.giftcoupon.domain.repository.UserRepository;
import com.company.demo.giftcoupon.outbox.domain.entity.TryIssueCouponCommand;
import com.company.demo.giftcoupon.outbox.domain.event.CouponIssuedEvent;
import com.company.demo.giftcoupon.outbox.domain.event.CouponIssuedPayload;
import com.company.demo.giftcoupon.outbox.domain.event.DomainEventEnvelope;
import com.company.demo.giftcoupon.outbox.domain.result.CouponIssuanceResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.company.demo.giftcoupon.domain.repository.CouponRepository;

import java.time.LocalDateTime;


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