package com.hls.utils;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
public class singerTopN {

    private final RedisTemplate redisTemplate;


    public void add(String key, String value,double score) {
        if(key.isEmpty()){
            return;
        }
        ZSetOperations z = redisTemplate.opsForZSet();
        z.add(key,value,score);
    }


    public void delete(String key,String value) {
        if(key.isEmpty()){
            return;
        }
        ZSetOperations z = redisTemplate.opsForZSet();
        z.remove(key,value);
    }


}
