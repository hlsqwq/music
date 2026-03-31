package com.hls.base.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.List;

@Configuration
public class LuaScriptConfig {


    @Bean
    public DefaultRedisScript<Integer> hotTaskScript() {
        DefaultRedisScript<Integer> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("scripts/hotTask.lua"));
        script.setResultType(Integer.class);
        return script;
    }

    @Bean
    public DefaultRedisScript<Long> incrOrDecrLikeNum() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("scripts/incrOrDecrLikeNum.lua"));
        script.setResultType(Long.class);
        return script;
    }


}