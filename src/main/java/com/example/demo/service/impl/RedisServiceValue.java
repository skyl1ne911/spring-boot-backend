package com.example.demo.service.impl;

import com.example.demo.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service("redisServiceValue")
public class RedisServiceValue extends RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public <T> void save(String key, T value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public <T> void save(String key, T value, Duration duration) {
        redisTemplate.opsForValue().set(key, value, duration);
    }

    @SuppressWarnings("unchecked")
    public <T> T findByKey(String key) {
        return (T) redisTemplate.opsForValue().get(key);
    }

    public Object delete(String key) {
        return redisTemplate.delete(key);
    }

}
