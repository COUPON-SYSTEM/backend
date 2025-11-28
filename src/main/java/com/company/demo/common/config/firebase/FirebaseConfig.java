package com.company.demo.common.config.firebase;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.sdk.path}")
    private String serviceAccountKeyPath;

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        // 서비스 계정 키 파일 로드
        Resource resource = new ClassPathResource(serviceAccountKeyPath);

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(resource.getInputStream()))
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            // FirebaseApp이 초기화되어 있지 않다면 초기화
            return FirebaseApp.initializeApp(options);
        }
        // 이미 초기화되었다면 기존 인스턴스 반환
        return FirebaseApp.getInstance();
    }

    @Bean
    public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        // 푸시 알림 발송을 위한 FirebaseMessaging 인스턴스 빈으로 등록
        return FirebaseMessaging.getInstance(firebaseApp);
    }
}