package com.company.demo.giftcoupon.batch;

import org.springframework.batch.item.ItemReader;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisCouponReader implements ItemReader<String> {

    private final RedisTemplate<String, String> redisTemplate;

    public RedisCouponReader(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String read() {
        return redisTemplate.opsForList().leftPop("coupon:queue");
    }
}
