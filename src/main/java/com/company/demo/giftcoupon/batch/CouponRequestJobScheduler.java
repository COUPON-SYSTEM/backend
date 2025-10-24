package com.company.demo.giftcoupon.batch;

import com.company.demo.common.constant.EventType;
import com.company.demo.common.constant.RedisKey;
import com.company.demo.giftcoupon.domain.repository.CouponRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponRequestJobScheduler {

    private static final long MAX_TOTAL = 100L; // 누적 한도
    private static final Duration INTERVAL = Duration.ofSeconds(10); // 10초 주기

    private final JobLauncher jobLauncher;
    private final Job couponRequestJob;
    private final RedisTemplate<String, String> redisTemplate;
    private final ThreadPoolTaskScheduler couponTaskScheduler;
    private final CouponRepository couponRepository;

<<<<<<< HEAD
    private volatile ScheduledFuture<?> future;

    /** 애플리케이션 기동 시 스케줄 등록 */
    @PostConstruct
    public void start() {
        if (future == null || future.isCancelled()) {
            future = couponTaskScheduler.scheduleAtFixedRate(this::tick, INTERVAL);
            log.info("[CouponScheduler] started: fixedRate={}s", INTERVAL.getSeconds());
        }
    }

    /** 안전 종료 */
    @PreDestroy
    public void shutdown() {
        cancel();
    }

    /** 한 번의 주기 동작 */
    private void tick() {
=======
    @Scheduled(fixedRate = 5000) // 1초마다 실행
    public void runJob() {
>>>>>>> origin
        try {
            long issued = couponRepository.countIssuedByEventType(EventType.ISSUED_EVENT);
            long len = Optional.ofNullable(
                    redisTemplate.opsForList().size(RedisKey.COUPON_REQUEST_QUEUE_KEY)
            ).orElse(0L);

            // 1) 발급 100개 달성 + 큐 비었으면 중단
            if (issued >= MAX_TOTAL && len == 0L) {
                log.info("[CouponScheduler] issued={}, queue={}, stop.", issued, len);
                cancel();
                return;
            }

            if (len == 0L) {
                log.debug("[CouponScheduler] queue empty. skip running job.");
                return;
            }

            // 3) 배치 실행
            JobParameters params = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis()) // 매 실행 다른 파라미터
                    .toJobParameters();

            jobLauncher.run(couponRequestJob, params);

        } catch (Exception e) {
            log.error("[CouponScheduler] Failed to run job", e);
        }
    }

    /** 누적 발급수 조회 (없으면 0) */
    private long getTotalCount() {
        String v = redisTemplate.opsForValue().get(RedisKey.COUPON_TOTAL_COUNT_KEY);
        return (v == null) ? 0L : Long.parseLong(v);
    }

    /** 스케줄 자체 중단 */
    public void cancel() {
        ScheduledFuture<?> f = this.future;
        if (f != null && !f.isCancelled()) {
            f.cancel(true);
            log.info("[CouponScheduler] canceled.");
        }
    }

    /** 외부에서 재개하고 싶을 때 호출 */
    public void resume() {
        if (future == null || future.isCancelled()) {
            future = couponTaskScheduler.scheduleAtFixedRate(this::tick, INTERVAL);
            log.info("[CouponScheduler] resumed: fixedRate={}s", INTERVAL.getSeconds());
        }
    }
}
