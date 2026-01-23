package com.hls.media.config;


import com.xxl.job.core.context.XxlJobHelper;
import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

@Component
public class ThreadPool {


    @Bean
    public ThreadPoolExecutor threadPoolExecutor(){
        int core = Runtime.getRuntime().availableProcessors();
        return new ThreadPoolExecutor(
        core,        // 核心线程数（常驻线程，即使空闲也不会销毁）
        core*2,     // 最大线程数（线程池允许的最大并发线程数）
        60L,      // 非核心线程空闲超时时间（超时后销毁）
        TimeUnit.SECONDS,           // keepAliveTime的时间单位（秒/毫秒等）
        new ArrayBlockingQueue<>(1000),  // 任务队列（核心线程忙时，任务暂存到队列）
        Executors.defaultThreadFactory(),        // 线程工厂（自定义线程命名、优先级等）
        new ThreadPoolExecutor.AbortPolicy()    // 拒绝策略（任务满时的处理方式）
        );
    }
}
