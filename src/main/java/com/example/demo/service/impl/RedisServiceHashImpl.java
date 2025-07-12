package com.example.demo.service.impl;

import com.example.demo.service.RedisServiceHash;
import org.springframework.stereotype.Service;

@Service
public class RedisServiceHashImpl extends RedisServiceHash {

    @Override
    public <T> void save(String key, String hashKey, T value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    @Override
    public <T> T findByKey(String key, String hashKey) {
        return (T) redisTemplate.opsForHash().get(key, hashKey);
    }

    @Override
    public void delete(String key, String... hashKey) {
        redisTemplate.opsForHash().delete(key, hashKey);
    }

}
