package com.hls.base.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Autowired
    private UserInterceptor userInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userInterceptor)
                .addPathPatterns("/**") // 拦截所有路径
                .excludePathPatterns(
                    "/login", 
                    "/auth/login",
                    "/error",
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/user/register"
                ); // 显式放行登录、注册和错误路径
    }
}