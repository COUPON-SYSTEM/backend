package com.company.demo.giftcoupon.handler.sevice;

import com.company.demo.common.response.error.ErrorCode;
import com.company.demo.common.response.exception.BusinessException;
import com.company.demo.giftcoupon.domain.entity.User;
import com.company.demo.giftcoupon.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Value("${spring.mail.admin.email}")
    private String adminEmail;

    public String getFcmToken(Long userId){
        String fcmToken = userRepository.findFcmTokenByUserId(userId).orElseGet(null);
                //.orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_FCMTOKEN)); 프론트 코드 변경시 연결

        return fcmToken;
    }

    public String getEmail(Long userId){
        String email = userRepository.findEmailByUserId(userId).orElse(adminEmail); // 나중엔 삭제할 예정
        return email;
    }

    public User findById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_USER));
    }
}
