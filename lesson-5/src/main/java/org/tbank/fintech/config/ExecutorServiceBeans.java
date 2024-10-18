package org.tbank.fintech.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class ExecutorServiceBeans {

    @Bean(name = "initExecutorService")
    public ExecutorService initExecutorService(
            @Value("${executors.init.count-of-threads}") int countOfThreads
    ) {
        return Executors.newFixedThreadPool(countOfThreads, new ThreadFactory() {
            private final AtomicInteger count = new AtomicInteger(1);
            @Override
            public Thread newThread(@NotNull Runnable r) {
                return new Thread(r, "RepositoryInitThreadPool-" + count.getAndIncrement());
            }
        });
    }

    @Bean(name = "schedulerExecutorService")
    public ScheduledExecutorService schedulerExecutorService(
            @Value("${executors.scheduled.count-of-threads}") int countOfThreads
    ) {
        return Executors.newScheduledThreadPool(countOfThreads, new ThreadFactory() {
            private final AtomicInteger count = new AtomicInteger(1);
            @Override
            public Thread newThread(@NotNull Runnable r) {
                return new Thread(r, "InitSchedulerThreadPool-" + count.getAndIncrement());
            }
        });
    }


}
