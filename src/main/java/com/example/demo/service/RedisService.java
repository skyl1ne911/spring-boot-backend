package com.example.demo.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;

public abstract class RedisService {

    @Autowired
    protected RedisTemplate<String, Object> redisTemplate;

    public abstract <T> void save(String key, T value);

    public abstract <T> void save(String key, T value, Duration duration);

    public abstract <T> T findByKey(String key);

    public abstract Object delete(String key);
}
