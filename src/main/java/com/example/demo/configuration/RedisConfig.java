package com.example.demo.configuration;


import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.databind.ser.std.ByteArraySerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.lettuce.core.ReadFrom;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.RedisStaticMasterReplicaConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.time.Duration;


@Configuration
public class RedisConfig {

//    @Bean
//    public RedisConnectionFactory redisConnectionFactory() {
//        RedisStaticMasterReplicaConfiguration serverConfig = new RedisStaticMasterReplicaConfiguration("localhost", 6379);
////        serverConfig.addNode("localhost", 6380);
//
////        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
////                .readFrom(ReadFrom.MASTER_PREFERRED)
////                .build();
//
//        return new LettuceConnectionFactory(serverConfig);
//    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redis = new RedisTemplate<>();
        redis.setConnectionFactory(redisConnectionFactory);


        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//        mapper.activateDefaultTyping(
//                LaissezFaireSubTypeValidator.instance,
//                ObjectMapper.DefaultTyping.NON_FINAL,
//                JsonTypeInfo.As.PROPERTY
//        );
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
//        GenericJackson2JsonRedisSerializer jsonRedisSerializer = new GenericJackson2JsonRedisSerializer(mapper);

        JdkSerializationRedisSerializer serializer = new JdkSerializationRedisSerializer();


        redis.setKeySerializer(stringRedisSerializer);
        redis.setHashKeySerializer(stringRedisSerializer);

        redis.setValueSerializer(serializer);
        redis.setHashValueSerializer(serializer);

        redis.afterPropertiesSet();
        return redis;
    }

}
