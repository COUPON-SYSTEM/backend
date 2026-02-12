package com.company.demo.common.config.redis;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Slf4j
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port:6379}")
    private int port;

    @PostConstruct
    public void init() {
        log.info("Redis Host: {}", host);
        log.info("Redis Port: {}", port);
    }

    // redis-cli로 사람이 읽을 수 있게: String <-> String
    @Bean("customStringRedisTemplate")
    @Primary
    public RedisTemplate<String, String> stringTemplate(RedisConnectionFactory cf) {
        RedisTemplate<String, String> t = new RedisTemplate<>();
        t.setConnectionFactory(cf);

        StringRedisSerializer s = new StringRedisSerializer();
        t.setKeySerializer(s);
        t.setValueSerializer(s);
        t.setHashKeySerializer(s);
        t.setHashValueSerializer(s);

        t.afterPropertiesSet();
        return t;
    }

    // Object는 JSON으로 저장: redis-cli에서도 JSON 문자열로 확인 가능
    @Bean("objectRedisTemplate")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory cf) {
        RedisTemplate<String, Object> t = new RedisTemplate<>();
        t.setConnectionFactory(cf);

        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();

        t.setKeySerializer(stringSerializer);
        t.setHashKeySerializer(stringSerializer);
        t.setValueSerializer(jsonSerializer);
        t.setHashValueSerializer(jsonSerializer);

        t.afterPropertiesSet();
        return t;
    }
}
