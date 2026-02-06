package com.company.demo.common.config.batch;

import com.company.demo.giftcoupon.batch.*;
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
                .skip(Exception.class)
                .skipLimit(10)
                .build();
    }
}