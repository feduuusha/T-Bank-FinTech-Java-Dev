package org.tbank.fintech.lesson_9.listener;

import jakarta.persistence.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.TimeUnit;

@Slf4j
public class StatisticEntityListener {
    private long countOfPersistOperations = 0;
    private long countOfUpdateOperations = 0;
    private long countOfRemoveOperations = 0;
    private long countOfLoadOperations = 0;

    @PostPersist
    public void postPersist(Object o) {
        ++countOfPersistOperations;
    }

    @PostUpdate
    public void preUpdate(Object o) {
        ++countOfUpdateOperations;
    }

    @PostRemove
    public void postRemove(Object o) {
        ++countOfRemoveOperations;
    }

    @PostLoad
    public void postLoad(Object o) {
        ++countOfLoadOperations;
    }

    @Scheduled(fixedRate = 10, initialDelay = 10, timeUnit = TimeUnit.MINUTES)
    public void statisticLogger() {
        log.info("Statistic of DB operations: \n"
                + "Count of persist operations " + countOfPersistOperations + "\n"
                + "Count of update operations " + countOfUpdateOperations + "\n"
                + "Count of remove operations " + countOfRemoveOperations + "\n"
                + "Count of load operations " + countOfLoadOperations
        );
    }
}
