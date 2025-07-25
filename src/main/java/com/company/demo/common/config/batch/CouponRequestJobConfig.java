package com.company.demo.common.config.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.batch.core.Job;
import com.company.demo.giftcoupon.batch.RedisCouponReader;

@Configuration
@RequiredArgsConstructor
public class CouponRequestJobConfig {
    private final RedisCouponReader redisCouponReader;
    private final KafkaCouponPublisher kafkaCouponPublisher;
    private final CouponWriter couponWriter;

    @Bean
    public Job couponRequestJob(JobBuilderFactory jobBuilderFactory,
                              StepBuilderFactory stepBuilderFactory) {

        Step step = stepBuilderFactory.get("couponIssueStep")
                .<String, CouponRequest>chunk(10)
                .reader(redisCouponReader)
                .processor(kafkaCouponPublisher)
                .writer(couponWriter)
                .build();

        return jobBuilderFactory.get("couponIssueJob")
                .start(step)
                .build();
    }
}
