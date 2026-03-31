package com.hls.base.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private String port;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        // 这里注意：如果是 SSL 连接用 rediss://，普通连接用 redis://
        config.useSingleServer()
              .setAddress("redis://" + host + ":" + port)
              // .setPassword("xxx") 
              .setDatabase(0);
        
        // 设置看门狗（Watchdog）的检查间隔，默认是 30000ms
        // config.setLockWatchdogTimeout(30000); 

        return Redisson.create(config);
    }
}