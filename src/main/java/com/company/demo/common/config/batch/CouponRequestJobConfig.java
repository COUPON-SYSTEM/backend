package com.company.demo.common.config.batch;

import com.company.demo.giftcoupon.batch.*;
import com.company.demo.giftcoupon.batch.exception.CouponSkipListener;
import com.company.demo.giftcoupon.batch.exception.RetryInfraException;
import com.company.demo.giftcoupon.batch.exception.SkipDataException;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.batch.core.Job;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class CouponRequestJobConfig {
    private final RedisCouponReader redisCouponReader;
    private final CouponIssueProcessor couponIssueProcessor;
    private final CouponIssuedWriter couponIssuedWriter;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final CouponSkipListener couponSkipListener;

    @Bean
    public Job couponIssueJob() {
        return new JobBuilder("couponIssueJob", jobRepository)
                .start(couponIssueStep())
                .build();
    }

    @Bean
    public Step couponIssueStep() {
        return new StepBuilder("couponIssueStep", jobRepository)
                .<CouponIssueInput, ProcessedCouponData>chunk(10, transactionManager)
                .reader(redisCouponReader)
                .processor(couponIssueProcessor)
                .writer(couponIssuedWriter)
                .faultTolerant()

                // 인프라성 예외는 retry
                .retry(RetryInfraException.class)
                .retryLimit(3)
                .noRetry(SkipDataException.class)

                // 데이터성 예외만 skip
                .skip(SkipDataException.class)
                .skip(RetryInfraException.class)
                .skipLimit(30)
                .listener(couponSkipListener)
                .noRollback(SkipDataException.class)

                .build();
    }
}