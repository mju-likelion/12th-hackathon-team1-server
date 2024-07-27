package com.hackathonteam1.refreshrator.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    private final String redisHost;
    private final int redisPort;

    public RedisConfig(@Value("${spring.data.redis.host}") String redisHost,
                       @Value("${spring.data.redis.port}")int redisPort){
        this.redisHost = redisHost;
        this.redisPort = redisPort;
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory(){
        return new LettuceConnectionFactory(redisHost, redisPort);
    }

    @Bean
    public <K, V> RedisTemplate<K, V> redisTemplate(RedisConnectionFactory redisConnectionFactory){
        RedisTemplate<K, V> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer()); //key를 string으로 시리얼라이즈
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer()); //value를 json으로 시리얼라이즈
        redisTemplate.setHashKeySerializer(new StringRedisSerializer()); //Hash의 key를 String으로 시리얼라이즈
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer()); //Hash의 value를 json으로 시리얼라이즈
        return redisTemplate;
    }
}
