package com.company.demo.giftcoupon.batch;

import com.company.demo.common.constant.RedisKey;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Component
@RequiredArgsConstructor
public class RedisCouponReader implements ItemReader<String> {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String LUA_SCRIPT_PATH = "scripts/coupon_pop.lua";
    private final List<String> buffer = new ArrayList<>();
    private int cursor = 0;
    private final int BATCH_SIZE = 10;

    private DefaultRedisScript<List> luaPopScript;

    @PostConstruct
    public void initLuaScript() throws IOException {
        luaPopScript = new DefaultRedisScript<>();
        luaPopScript.setLocation(new ClassPathResource(LUA_SCRIPT_PATH));
        luaPopScript.setResultType(List.class);
    }

    /**
     *
     * 1. chunkSize=10, pop=1
     * 이러한 1개씩 pop하고 10개씩 read
     * Redis 접근 10번
     *
     * 2. chunkSize=10, pop=10
     * 10개씩 pop하고 10개씩 read
     * Redis 접근 1번
     *
     */
    @Override
    public String read() {
        if (cursor >= buffer.size()) {
            buffer.clear();
            cursor = 0;

            List<String> result = (List<String>) redisTemplate.execute(
                    luaPopScript,
                    Collections.singletonList(RedisKey.COUPON_REQUEST_QUEUE_KEY),
                    String.valueOf(BATCH_SIZE)
            );

            if (result != null) {
                buffer.addAll(result);
            }

            if (buffer.isEmpty()) {
                return null;
            }
        }

        return buffer.get(cursor++);
    }
}
