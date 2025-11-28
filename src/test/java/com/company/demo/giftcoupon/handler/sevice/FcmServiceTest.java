package com.company.demo.giftcoupon.handler.sevice;

import static org.junit.jupiter.api.Assertions.*;

import com.google.firebase.messaging.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FcmService 단위 테스트")
class FcmServiceTest {

    @Mock
    private FirebaseMessaging firebaseMessaging;

    @InjectMocks
    private FcmService fcmService;

    private final String TEST_TOKEN = "test_fcm_token";
    private final String TEST_TITLE = "Test Title";
    private final String TEST_BODY = "Test Body";

    @Test
    @DisplayName("FCM 메시지 발송에 성공해야 한다")
    void testSendNotification_Success() throws FirebaseMessagingException {
        when(firebaseMessaging.send(any(Message.class))).thenReturn("projects/project-id/messages/success-id");

        assertDoesNotThrow(() -> fcmService.sendNotification(TEST_TOKEN, TEST_TITLE, TEST_BODY));

        verify(firebaseMessaging, times(1)).send(any(Message.class));
    }

    @Test
    @DisplayName("FCM 발송 실패 시 예외를 던져야 한다")
    void testSendNotification_Failure() throws FirebaseMessagingException {
        FirebaseMessagingException mockException = mock(FirebaseMessagingException.class);

        doThrow(mockException)
                .when(firebaseMessaging).send(any(Message.class));

        assertThrows(FirebaseMessagingException.class,
                () -> fcmService.sendNotification(TEST_TOKEN, TEST_TITLE, TEST_BODY));

        verify(firebaseMessaging, times(1)).send(any(Message.class));
    }
}