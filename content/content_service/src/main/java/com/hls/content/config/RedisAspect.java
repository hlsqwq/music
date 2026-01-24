package com.hls.content.config;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.TimeUnit;


@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RedisAspect {

    private final RedisTemplate<String, Object> redisTemplate;


    @Pointcut("@annotation(com.hls.content.config.Redis)")
    public void redisCheckMethod() {}



    @Around("redisCheckMethod()")
    public Object redisCheck(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Redis annotation = methodSignature.getMethod().getAnnotation(Redis.class);
        if(annotation != null){
            String type = annotation.type();
            String key = annotation.key();
            if(type.equals("get")){
                Object o = redisTemplate.opsForValue().get(key);
                if(o!=null){
                    return o;
                }
                Object proceed = pjp.proceed();
                redisTemplate.opsForValue().set(key,proceed,30+new Random().nextInt(10),TimeUnit.MINUTES);
                return proceed;
            } else if (type.equals("post")) {
                redisTemplate.delete(key);
            }else{
                log.error("注解类型错误,type:{},key:{}",type,key);
            }
        }
        return pjp.proceed();
    }



}
