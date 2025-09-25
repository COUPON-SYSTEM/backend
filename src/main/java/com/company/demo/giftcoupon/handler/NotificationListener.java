package com.company.demo.giftcoupon.handler;

import com.company.demo.common.response.error.ErrorCode;
import com.company.demo.giftcoupon.event.CouponIssuedEvent;
import com.company.demo.giftcoupon.exception.InternalServerErrorException;
import com.company.demo.giftcoupon.handler.CouponEventHandler;
import com.company.demo.giftcoupon.handler.sevice.EmailService;
import com.company.demo.giftcoupon.handler.sevice.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

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

            log.info("사용자 알림 전송 성공 - CouponId: {}", event.getCouponId());
        } catch (Exception e) {
            log.error("사용자 알림 전송 실패 - CouponId: {}", event.getCouponId(), e.getMessage(), e);
            throw new InternalServerErrorException(ErrorCode.NOTIFICATION_SEND_FAILED);
        }
    }
}
