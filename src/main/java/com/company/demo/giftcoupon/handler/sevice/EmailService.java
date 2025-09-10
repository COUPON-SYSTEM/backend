package com.company.demo.giftcoupon.handler.sevice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        try {
            mailSender.send(message);
            log.info("이메일 전송 성공: 받는 사람={}, 제목={}", to, subject);
        } catch (Exception e) {
            log.error("이메일 전송 실패: 받는 사람={}, 에러={}", to, e.getMessage());
            throw new RuntimeException("이메일 전송 실패", e);
        }
    }
}
