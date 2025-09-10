package com.company.demo.giftcoupon.event;

import java.time.LocalDateTime;

public interface FinalizableEvent { // TODO: 이벤트의 공통처리를 위한 인터페이스 - zero payload의 필요성
    Long getCouponId();
    Long getUserId();
    String getCouponCode();
    LocalDateTime getEventTime();
    String getEventType();
}
