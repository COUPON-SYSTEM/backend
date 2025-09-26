package com.company.demo.giftcoupon.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponRequestJobScheduler {

    private final JobLauncher jobLauncher;
    private final Job couponRequestJob;

    @Scheduled(fixedRate = 5000) // 1초마다 실행
    public void runJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis()) // 매번 다른 JobParameters 필요
                    .toJobParameters();

            jobLauncher.run(couponRequestJob, jobParameters);
        } catch (Exception e) {
            // 예외 로깅 필수
            log.error("Failed to run couponRequestJob", e);
        }
    }
}
