package com.company.demo.giftcoupon.batch;

import com.company.demo.common.constant.RedisKey;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemReader;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class RedisCouponReader implements ItemReader<String> {

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public String read() {
        return redisTemplate.opsForList().rightPop(RedisKey.COUPON_REQUEST_QUEUE_KEY);
    }
}
