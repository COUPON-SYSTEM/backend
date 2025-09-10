package com.company.demo.giftcoupon.sevice;

import com.company.demo.giftcoupon.event.CouponIssuedEvent;
import com.company.demo.giftcoupon.handler.CouponEventHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationListener implements CouponEventHandler {

    private final EmailService emailService;
    private final SmsService smsService;

    @Override
    @Async
    public void handle(CouponIssuedEvent event) {
        notification(event);
    }

    public void notification(CouponIssuedEvent event){
        String userEmail = event.getUserEmail();
        String userPhone = event.getUserPhone();
        String message = "새로운 쿠폰이 발급되었습니다!";

        try {
            emailService.sendEmail(userEmail, "쿠폰 발급 알림", message);
            smsService.sendSms(userPhone, message);

            log.info("사용자 알림 전송 완료");
        } catch (Exception e) {
            log.error("알림 전송 실패 - CouponId: {}", event.getCouponId(), e);
            // exception
        }
    }
}
