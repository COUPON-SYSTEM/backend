package com.company.demo.giftcoupon.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemReader;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RedisCouponReader implements ItemReader<List<String>> {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String LUA_POP_10 = """
        local items = redis.call('LRANGE', KEYS[1], 0, 9)
        if #items > 0 then
            redis.call('LTRIM', KEYS[1], 10, -1)
        end
        return items
    """;

    @Override
    public List<String> read() {
        List<String> users = redisTemplate.execute(
                new DefaultRedisScript<>(LUA_POP_10, List.class),
                Collections.singletonList("coupon:queue")
        );
        return (users == null || users.isEmpty()) ? null : users;
    }
}
