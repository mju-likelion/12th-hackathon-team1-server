package com.hackathonteam1.refreshrator.util;


import com.hackathonteam1.refreshrator.exception.RedisException;
import com.hackathonteam1.refreshrator.exception.errorcode.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
@Slf4j
public class RedisUtil<K, V> {
    private final RedisTemplate<K, V> redisTemplate;

    public void save(K key, V value, long timeout, TimeUnit timeUnit) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
        } catch (Exception e) {
            throw new RedisException( ErrorCode.REDIS_ERROR, e.getMessage());
        }
    }

    public void delete(K key) {
        try {
            redisTemplate.delete(key);
        }catch (Exception e){
            throw new RedisException( ErrorCode.REDIS_ERROR, e.getMessage());
        }
    }

    public Optional<V> findById(K key){
        V result = redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(result);
    }
}
