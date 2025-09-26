package com.company.demo.common.config.async;

import com.company.demo.giftcoupon.exception.DataNotFoundException;
import com.company.demo.giftcoupon.exception.ExternalApiConnectionException;
import com.company.demo.giftcoupon.exception.InternalServerErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig implements AsyncConfigurer { // 처리하지 못한 비동기 응답 catch
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) -> {
            if (ex instanceof DataNotFoundException) {
                log.warn("데이터 오류 발생: {}", ex.getMessage());
            } else if (ex instanceof ExternalApiConnectionException) {
                log.error("외부연동 실패 오류: {}", ex);
            } else if (ex instanceof InternalServerErrorException) {
                log.error("내부연동 실패 오류: {}", ex);
            }
            else {
                log.error("알 수 없는 오류 발생", ex);
            }
        };
    }
}
