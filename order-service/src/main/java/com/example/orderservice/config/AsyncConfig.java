package com.example.orderservice.config;

import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {

    @Bean(name = "orderExecutor")
    public Executor orderExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);        // Always running threads
        executor.setMaxPoolSize(5);         // Max allowed threads
        executor.setQueueCapacity(50);      // Waiting tasks
        executor.setThreadNamePrefix("Order-Async-");

        executor.setTaskDecorator(runnable -> {
            var contextMap = MDC.getCopyOfContextMap();
            return () -> {
                if(contextMap != null) {
                    MDC.setContextMap(contextMap);
                }

                try {
                    runnable.run();
                } finally {
                    MDC.clear();
                }
            };
        });
        executor.initialize();
        return executor;
    }
}
