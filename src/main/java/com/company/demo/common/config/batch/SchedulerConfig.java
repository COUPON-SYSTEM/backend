package com.company.demo.common.config.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Slf4j
@Configuration
@EnableScheduling
public class SchedulerConfig {
    @Bean
    public ThreadPoolTaskScheduler couponTaskScheduler() {
        ThreadPoolTaskScheduler ts = new ThreadPoolTaskScheduler();
        ts.setPoolSize(1); // 작업 겹침이 없다면 1로 충분
        ts.setThreadNamePrefix("coupon-scheduler-");
        ts.initialize();
        ts.setRemoveOnCancelPolicy(true); // cancel 시 큐에서 제거(메모리 누수 방지)
        ts.setWaitForTasksToCompleteOnShutdown(false); // 빠른 종료
        ts.setErrorHandler(t -> log.error("CouponRequestScheduler uncaught error", t));
        return ts;
    }
}