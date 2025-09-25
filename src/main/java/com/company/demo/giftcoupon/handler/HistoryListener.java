package com.company.demo.giftcoupon.handler;

import com.company.demo.giftcoupon.domain.entity.History;
import com.company.demo.giftcoupon.domain.repository.HistoryRepository;
import com.company.demo.giftcoupon.event.CouponIssuedEvent;
import com.company.demo.giftcoupon.handler.CouponEventHandler;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@Slf4j
@RequiredArgsConstructor
public class HistoryListener implements CouponEventHandler {

    private final HistoryRepository historyRepository;


    @Override
    @Async
    public void handle(CouponIssuedEvent event) {
        issuedHistorySave(event);
    }

    @Transactional
    public void issuedHistorySave(CouponIssuedEvent event){
        try {
            // TODO: 통합관리할지
            History history = new History(event.getCouponId(), event.getUserId(), event.getMessage());
            historyRepository.save(history);
            log.info("쿠폰 발급 이력 저장 성공 - CouponId: {}", event.getCouponId());
        } catch (Exception e) {
            log.error("쿠폰 발급 이력 저장 실패 - CouponId: {}", event.getCouponId(), e.getMessage(), e);
        }
    }
}
