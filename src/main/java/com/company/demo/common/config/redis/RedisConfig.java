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

    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.database:0}")
    private int redisDatabase;

    @PostConstruct
    public void init() {
        log.info("Redis Host: {}", host);
        log.info("Redis Port: {}", port);
    }

    // RedisProperties로 yaml에 저장한 host, post를 연결
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {

        // Redis 연결 설정을 위한 객체를 생성
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);

        config.setDatabase(redisDatabase);

        return new LettuceConnectionFactory(config);
    }

    // serializer 설정으로 redis-cli를 통해 직접 데이터를 조회할 수 있도록 설정
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

    @Bean("objectRedisTemplate")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory cf) {
        RedisTemplate<String, Object> t = new RedisTemplate<>();
        t.setConnectionFactory(cf);

        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        // Object 저장을 위해 Jackson 기반 Serializer 사용
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();

        // 키는 String으로 유지
        t.setKeySerializer(stringSerializer);
        t.setHashKeySerializer(stringSerializer);

        // 값은 JSON/Object 직렬화 사용
        t.setValueSerializer(jsonSerializer);
        t.setHashValueSerializer(jsonSerializer);

        t.afterPropertiesSet();
        return t;
    }
}
