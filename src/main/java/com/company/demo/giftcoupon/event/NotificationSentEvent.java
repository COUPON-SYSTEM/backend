package com.company.demo.giftcoupon.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationSentEvent implements FinalizableEvent {
    private Long couponId;
    private Long userId;
    private String couponCode;
    private String notificationType;
    private LocalDateTime sentAt;

    @Override
    public LocalDateTime getEventTime() {
        return sentAt;
    }

    @Override
    public String getEventType() {
        return "NOTIFICATION_SENT";
    }
}
