package com.company.demo.giftcoupon.handler;

import com.company.demo.common.constant.GroupType;
import com.company.demo.common.constant.KafkaTopic;
import com.company.demo.giftcoupon.handler.sevice.EmailService;
import com.company.demo.giftcoupon.handler.sevice.FcmService;
import com.company.demo.giftcoupon.handler.sevice.UserService;
import com.company.demo.giftcoupon.outbox.domain.event.CouponIssuedPayload;
import com.company.demo.giftcoupon.outbox.domain.event.DomainEventEnvelope;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper;

    @Override
    @KafkaListener(
            topics = KafkaTopic.COUPON_ISSUED,
            groupId = GroupType.NOTIFICATION,
            containerFactory = "couponIssueKafkaListenerContainerFactory"
    )
    public void handle(DomainEventEnvelope<?> envelope) {
        notification(envelope);
    }

    private void notification(DomainEventEnvelope<?> envelope) {
        CouponIssuedPayload payload = objectMapper.convertValue(
                envelope.payload(),
                CouponIssuedPayload.class
        );

        log.info("[알림 핸들러]");
        String fcmToken = userService.getFcmToken(payload.userId());
        String message = "새로운 [" + payload.couponId() + "] 쿠폰이 발급되었습니다!";
        String title = "쿠폰 도착 알림";
        String userEmail = userService.getEmail(payload.userId());

        try {
            emailService.sendEmail(userEmail, "쿠폰 발급 알림", message);
            log.info("fcmToken = {}", fcmToken);
            if (fcmToken != null && !fcmToken.isEmpty()) {
                fcmService.sendNotification(fcmToken, title, message);
            }
            log.info("사용자 알림 전송 성공 - CouponId: {}", payload.couponId());
        } catch (Exception e) {
            log.error("사용자 알림 전송 실패 - CouponId: {}", payload.couponId(), e.getMessage(), e);
        }
    }

    @KafkaListener(
            topics = KafkaTopic.COUPON_ISSUED + ".DLT",
            groupId = GroupType.NOTIFICATION_DLQ,
            containerFactory = "dlqListenerContainerFactory"
    )
    public void handleHistoryDLQ(DomainEventEnvelope<?> envelope) {
        if (envelope == null || envelope.payload() == null) {
            log.warn("DLT에서 null 메시지 발견, 스킵");
            return;
        }

        try {
            CouponIssuedPayload payload = objectMapper.convertValue(
                    envelope.payload(),
                    CouponIssuedPayload.class
            );

            String fcmToken = userService.getFcmToken(payload.userId());
            String message = "새로운 [" + payload.couponId() + "] 쿠폰이 발급되었습니다!";
            String title = "쿠폰 도착 알림";
            String userEmail = userService.getEmail(payload.userId());

            emailService.sendEmail(userEmail, "쿠폰 발급 알림", message);
            log.info("fcmToken = {}", fcmToken);
            if (fcmToken != null && !fcmToken.isEmpty()) {
                fcmService.sendNotification(fcmToken, title, message);
            }
        } catch (Exception e) {
            log.error("DLT 처리 중 예외 발생 (무시됨) - EventId: {}, Error: {}",
                    envelope != null ? envelope.eventId() : "null",
                    e.getMessage(), e);
        }
    }
}
