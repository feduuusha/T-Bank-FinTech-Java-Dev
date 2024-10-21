package org.tbank.fintech.initialization;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ApplicationEventPublisher;
import org.tbank.fintech.config.ExecutorServiceBeans;

import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.times;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE ,classes = {RepositoryInitializationApplicationStartedEventListener.class, ExecutorServiceBeans.class})
public class RepositoryInitializationApplicationStartedEventListenerTests {

    @SpyBean
    private ScheduledExecutorService schedulerExecutorService;
    @MockBean
    private Runnable initializationTask;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Value("${executors.scheduled.task-execution-interval}")
    private Duration duration;

    @Test
    @DisplayName("Test for startScheduledTasks, eventPublisher publish ApplicationStartedEvent and RepositoryInitializationApplicationStartedEventListener should call startScheduledTasks method")
    public void startScheduledTasksTest() {
        // Arrange
        eventPublisher.publishEvent(ApplicationStartedEvent.class);

        // Act
        // Assert
        Mockito.verify(schedulerExecutorService, times(1)).scheduleAtFixedRate(initializationTask, 0,
                duration.toMillis(), TimeUnit.MILLISECONDS);
        Mockito.verify(initializationTask).run();
    }

}
