package com.company.demo.giftcoupon.sevice;

import com.company.demo.giftcoupon.event.CouponIssuedEvent;
import com.company.demo.giftcoupon.handler.CouponEventHandler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class HistoryListener implements CouponEventHandler {

    @Override
    @Async
    public void handle(CouponIssuedEvent event) {
        issuedHistorySave(event);
    }

    // TODO: Repository
    // TODO: Transactional 단위를 어떻게 할지
    public void issuedHistorySave(CouponIssuedEvent event){
        // TODO: Repository에 필요한 정보 저장
        // TODO: 저장되었다고 이벤트 발생?
    }
}
