package com.company.demo.giftcoupon.sevice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
class SmsService {
    public void sendSms(String to, String message) {
        try {
            // smsApiClient.send(to, message); // 실제 SMS API 호출 로직
            log.info("SMS 전송 성공: 받는 사람={}, 메시지={}", to, message);
        } catch (Exception e) {
            log.error("SMS 전송 실패: 받는 사람={}, 에러={}", to, e.getMessage());
            // 실패 시 재시도 로직 또는 알림 로직 추가
            throw new RuntimeException("SMS 전송 실패", e);
        }
    }
}
