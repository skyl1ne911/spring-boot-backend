package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;

public abstract class RedisServiceHash {

    @Autowired
    protected RedisTemplate<String, Object> redisTemplate;

    public abstract <T> void save(String key, String hashKey, T value);

    public abstract <T> T findByKey(String key, String hashKey);

    public abstract void delete(String key, String... hashKey);

}
