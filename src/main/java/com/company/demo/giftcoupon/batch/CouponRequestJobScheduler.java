package com.company.demo.giftcoupon.batch;

import com.company.demo.common.constant.RedisKey;
import com.company.demo.giftcoupon.domain.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponRequestJobScheduler {

    private final JobLauncher jobLauncher;
    private final Job couponRequestJob;
    private final RedisTemplate<String, String> redisTemplate;

    @Scheduled(fixedRate = 10000) // 10초마다 실행
    public void runJob() {
        long len = Optional.ofNullable(
                redisTemplate.opsForList().size(RedisKey.COUPON_REQUEST_QUEUE_KEY)
        ).orElse(0L);
        if (len == 0L) {
            log.debug("[CouponScheduler] queue empty. skip running job.");
            return;
        }
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
