package com.arsalan.tenanttable.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@EnableRetry
public class AsyncConfig {

    @Bean
    public Executor taskExecutor(){
        // Creates a thread pool for async tasks
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // Always keep at least 5 worker threads ready
        executor.setCorePoolSize(5);
        // Can grow up to 15 threads during heavy load
        executor.setMaxPoolSize(15);
        // Queue up to 120 tasks before creating extra threads
        executor.setQueueCapacity(120);
        // Prefix for easier debugging
        executor.setThreadNamePrefix("TenantTableTaskExecutor-");
        // Initializes and starts the thread pool
        executor.initialize();
        // Registers the executor as a Spring Bean
        return executor;
    }
}
