package com.company.demo.giftcoupon.handler;

import com.company.demo.common.constant.GroupType;
import com.company.demo.common.constant.KafkaTopic;
import com.company.demo.common.response.error.ErrorCode;
import com.company.demo.giftcoupon.event.CouponIssuedEvent;
import com.company.demo.giftcoupon.exception.InternalServerErrorException;
import com.company.demo.giftcoupon.handler.CouponEventHandler;
import com.company.demo.giftcoupon.handler.sevice.EmailService;
import com.company.demo.giftcoupon.handler.sevice.SmsService;
import com.company.demo.giftcoupon.outbox.domain.event.CouponIssuedPayload;
import com.company.demo.giftcoupon.outbox.domain.event.DomainEventEnvelope;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationListener implements CouponEventHandler {

    private final EmailService emailService;
    private final SmsService smsService;

    @Override
    @KafkaListener(
            topics = KafkaTopic.COUPON_ISSUED,
            groupId = GroupType.NOTIFICATION,
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handle(DomainEventEnvelope<CouponIssuedPayload> envelope) {
        notification(envelope);
    }

    private void notification(DomainEventEnvelope<CouponIssuedPayload> envelope){
        CouponIssuedPayload payload = envelope.payload();
        // TODO: 이벤트 아이디 가져오는 방법 고민
        String userEmail = "pyeonk@konkuk.ac.kr";
        // String userPhone = event.getUserPhone();
        String message = "새로운 쿠폰이 발급되었습니다!";

        try {
            emailService.sendEmail(userEmail, "쿠폰 발급 알림", message);
            // smsService.sendSms(userPhone, message);

            log.info("사용자 알림 전송 성공 - CouponId: {}", payload.couponId());
        } catch (Exception e) {
            log.error("사용자 알림 전송 실패 - CouponId: {}", payload.couponId(), e.getMessage(), e);
            // throw new InternalServerErrorException(ErrorCode.NOTIFICATION_SEND_FAILED);
            throw e;
        }
    }
}
