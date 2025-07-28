package com.company.demo.giftcoupon.batch;

import com.company.demo.common.constant.RedisKey;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemReader;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Component
@RequiredArgsConstructor
public class RedisCouponReader implements ItemReader<String> {

    private final RedisTemplate<String, String> redisTemplate;

    private final List<String> buffer = new ArrayList<>();
    private int cursor = 0;
    private final int BATCH_SIZE = 10;

    @Override
    public String read() {
        if (cursor >= buffer.size()) {
            buffer.clear();
            cursor = 0;

            // Lua Script 정의
            DefaultRedisScript<List> script = new DefaultRedisScript<>();
            script.setScriptText(
                    "local result = {} " +
                            "for i = 1, tonumber(ARGV[1]) do " +
                            "  local val = redis.call('RPOP', KEYS[1]) " +
                            "  if not val then break end " +
                            "  table.insert(result, val) " +
                            "end " +
                            "return result"
            );
            script.setResultType(List.class);

            List<String> result = (List<String>) redisTemplate.execute(
                    script,
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
