package com.hls.base.utils;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class MinioTempRedis {
    private final RedisTemplate<String, Boolean> redisTemplate;



    private String getKey(String fileMd5) {
        return "media_temp_"+fileMd5;
    }

    /**
     * 检查文件
     * @param fileMd5
     * @return
     */
    public boolean checkFile(String fileMd5) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue().get(getKey(fileMd5)));
    }


    /**
     * 设置文件存在
     * @param fileMd5
     */
    public void addFile(String fileMd5) {
        redisTemplate.opsForValue().set(getKey(fileMd5), true,5, TimeUnit.MINUTES);
    }

}
