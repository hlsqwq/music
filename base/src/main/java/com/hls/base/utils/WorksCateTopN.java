package com.hls.base.utils;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
public class WorksCateTopN {

    private final RedisTemplate redisTemplate;


    public void add(String key, int value,double score) {
        if(key.isEmpty()){
            return;
        }
        ZSetOperations z = redisTemplate.opsForZSet();
        z.add(key,value,score);
    }


    public void delete(String key,int value) {
        if(key.isEmpty()){
            return;
        }
        ZSetOperations z = redisTemplate.opsForZSet();
        z.remove(key,value);
    }


    /**
     *  key  categoryId_id_song/singer
     *  value    id(songId/singerId)
     *  score    hot
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set getTopRange(String key, int start, int end) {
        ZSetOperations z = redisTemplate.opsForZSet();
        return z.reverseRange(key,start,end);
    }


    /**
     * key  categoryId_id_song/singer
     * value    id(songId/singerId)
     * score    hot
     *
     * @param key
     * @param num
     * @return
     */
    public Set getTopN(String key, int num) {
        ZSetOperations z = redisTemplate.opsForZSet();
        return getTopRange(key,0,num);
    }


}
