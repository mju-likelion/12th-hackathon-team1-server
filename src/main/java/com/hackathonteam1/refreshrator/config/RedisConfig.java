package com.hackathonteam1.refreshrator.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

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
    public CacheManager redisCacheManager(){
        RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig() //Redis 캐시 설정을 정의하기 위한 클래스, 기본 캐시 구성 가져옴.
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())) //key를 String으로 직렬화
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer())) //value를 json으로 직렬화
                .entryTtl(Duration.ofMinutes(15L)); //캐시에 15분 저장

        return RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(redisConnectionFactoryForDb1()) //redis db1로 연결
                .cacheDefaults(configuration)
                .build();
    }

    @Bean
    @Primary
    public RedisConnectionFactory redisConnectionFactory(){
        return createLettuceConnectionFactory(0);
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactoryForDb1(){
        return createLettuceConnectionFactory(1);
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

    private LettuceConnectionFactory createLettuceConnectionFactory(int database){
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisHost, redisPort);
        lettuceConnectionFactory.setDatabase(database);
        return lettuceConnectionFactory;
    }
}