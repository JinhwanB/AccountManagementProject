package com.jh.accountmanagement.config;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisUtils {
    private final RedisTemplate<String, List<>> accountRedisTemplate;

    public void set(String key, Object o) {
        accountRedisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(o.getClass()));
        accountRedisTemplate.opsForValue().set(key, o, 30, TimeUnit.MINUTES);
    }

    public Object get(String key) {
        return accountRedisTemplate.opsForValue().get(key);
    }

    public void delete(String key) {
        if (hasKey(key)) {
            accountRedisTemplate.delete(key);
        }
    }

    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(accountRedisTemplate.hasKey(key));
    }

    public void update(String key, Object o) {
        if (hasKey(key)) {
            set(key, o);
        }
    }
}
