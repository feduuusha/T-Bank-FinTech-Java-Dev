package org.tbank.fintech.initialization;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class RepositoryInitializationApplicationStartedEventListener {

    private final ScheduledExecutorService schedulerExecutorService;
    private final Runnable initializationTask;

    @Value("${executors.scheduled.task-execution-interval}")
    private Duration taskExecutionIntervalDuration;

    @EventListener(classes = {ApplicationStartedEvent.class})
    public void startScheduledTasks() {
        log.info("Calling startScheduledTasks method on ApplicationStartedEvent");
        this.schedulerExecutorService.scheduleAtFixedRate(initializationTask, 0,
                taskExecutionIntervalDuration.toMillis(), TimeUnit.MILLISECONDS);
    }

}
