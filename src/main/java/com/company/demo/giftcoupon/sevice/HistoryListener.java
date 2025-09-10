package com.company.demo.giftcoupon.sevice;

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
        History history = new History(event.getCouponId(), event.getUserId(), event.getMessage());
        historyRepository.save(history);
        log.info("쿠폰 발급 이력이 저장되었습니다 = CouponId : {}", event.getCouponId());
    }
}
