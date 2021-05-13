package com.geoniuses.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author ：zyf
 * @date ：2020/4/23 14:17
 */
@Configuration
public class ThreadPoolConfig {
    /**
     * 核心线程数
     */
    @Value("${async.executor.thread.core_pool_size}")
    private int corePoolSize;

    /**
     * 最大线程数
     */
    @Value("${async.executor.thread.max_pool_size}")
    private int maxPoolSize;

    /**
     * 队列最大长度
     */
    @Value("${async.executor.thread.queue_capacity}")
    private int queueCapacity;

    /**
     * 线程池维护线程所允许的空闲时间
     */
    @Value("${async.executor.thread.keep_alive_seconds}")
    private int keepAliveSeconds;

    /**
     * 线程池对拒绝任务(无线程可用)的处理策略
     * 那么主线程会自己去执行该任务
     */
    private ThreadPoolExecutor.CallerRunsPolicy callerRunsPolicy = new ThreadPoolExecutor.CallerRunsPolicy();

    /**
     * //当core max 和 queue都满的情况下，抛弃旧的任务
     * 发布订阅拒绝策略，需要实时性
     */
    private ThreadPoolExecutor.DiscardOldestPolicy discardOldestPolicy = new ThreadPoolExecutor.DiscardOldestPolicy();

//    private String threadNamePrefix = "stomp_netty-";

    //websocket线程池
//    @Bean(name = "stompExecutor")
//    public ThreadPoolTaskExecutor asyncExecutor() {
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setCorePoolSize(5);
//        executor.setMaxPoolSize(100);
//        executor.setQueueCapacity(10000);
//        executor.setKeepAliveSeconds(30);
////        executor.setRejectedExecutionHandler(callerRunsPolicy);
//        executor.setThreadNamePrefix("stomp_netty-");
//        executor.initialize();
//        return executor;
//    }
    @Bean(name = "taskExecutor")
    @Primary
    public ThreadPoolTaskExecutor threadPoolExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds(keepAliveSeconds);
        executor.setRejectedExecutionHandler(callerRunsPolicy);
        executor.setThreadNamePrefix("kafka_netty-");
        executor.initialize();
        return executor;
    }
    //推送线程池
    @Bean(name = "pubSubPoolExecutor")
    public ThreadPoolTaskExecutor pubSubPoolExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(1000);
        executor.setKeepAliveSeconds(60);
        executor.setRejectedExecutionHandler(discardOldestPolicy);
        executor.setThreadNamePrefix("pub_sub_netty-");
        executor.initialize();
        return executor;
    }
}
