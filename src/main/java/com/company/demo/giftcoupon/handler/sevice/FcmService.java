package com.company.demo.giftcoupon.handler.sevice;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FcmService {

    private final FirebaseMessaging firebaseMessaging;

    /**
     * 단일 디바이스에 알림 발송
     * @param token 알림을 받을 사용자의 디바이스 토큰
     * @param title 알림 제목
     * @param body 알림 내용
     */
    public void sendNotification(String token, String title, String body) throws FirebaseMessagingException {
        Message message = Message.builder()
                .setToken(token) // 알림을 보낼 디바이스 토큰
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                //.putData("key1", "value1") // 앱 내에서 사용할 추가 데이터 (선택 사항)
                .build();

        try {
            String response = firebaseMessaging.send(message);
            log.info("Successfully sent message: {}", response);
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send Firebase message to token {}: {}", token, e.getMessage());
            throw e;
        }
    }
}
