package com.company.demo.giftcoupon.handler;

import com.company.demo.common.constant.GroupType;
import com.company.demo.common.constant.KafkaTopic;
import com.company.demo.giftcoupon.handler.sevice.EmailService;
import com.company.demo.giftcoupon.handler.sevice.FcmService;
import com.company.demo.giftcoupon.handler.sevice.UserService;
import com.company.demo.giftcoupon.outbox.domain.event.CouponIssuedPayload;
import com.company.demo.giftcoupon.outbox.domain.event.DomainEventEnvelope;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationListener implements CouponEventHandler {

    private final EmailService emailService;
    private final FcmService fcmService;
    private final UserService userService;

    @Override
    @KafkaListener(
            topics = KafkaTopic.COUPON_ISSUED,
            groupId = GroupType.NOTIFICATION,
            containerFactory = "couponIssueKafkaListenerContainerFactory"
    )
    public void handle(DomainEventEnvelope<CouponIssuedPayload> envelope) throws FirebaseMessagingException {
        notification(envelope);
    }

    private void notification(DomainEventEnvelope<CouponIssuedPayload> envelope) throws FirebaseMessagingException {
        CouponIssuedPayload payload = envelope.payload();

        String fcmToken = userService.getFcmToken(payload.userId());
        String message = "새로운 [" + payload.couponId() + "] 쿠폰이 발급되었습니다!";
        String title = "쿠폰 도착 알림";
        String userEmail = userService.getEmail(payload.userId());

        try {
            emailService.sendEmail(userEmail, "쿠폰 발급 알림", message);
            if (fcmToken != null) {
                fcmService.sendNotification(fcmToken, title, message);
            }
            log.info("사용자 알림 전송 성공 - CouponId: {}", payload.couponId());
        } catch (Exception e) {
            log.error("사용자 알림 전송 실패 - CouponId: {}", payload.couponId(), e.getMessage(), e);
            throw e; // DLT 적용
        }
    }
}
