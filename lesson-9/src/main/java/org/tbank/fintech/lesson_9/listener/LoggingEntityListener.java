package org.tbank.fintech.lesson_9.listener;

import jakarta.persistence.*;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

@Slf4j
public class LoggingEntityListener {

    private final HashMap<String, Long> startTimeOfOperation = new HashMap<>();
    @PrePersist
    public void prePersist(Object o) {
        log.info("Executing method persist with args: " + o.toString());
        startTimeOfOperation.put(o + "persist", System.currentTimeMillis());
    }

    @PreUpdate
    public void preUpdate(Object o) {
        log.info("Executing method update with args: " + o.toString());
        startTimeOfOperation.put(o + "update", System.currentTimeMillis());

    }

    @PreRemove
    public void preRemove(Object o) {
        log.info("Executing method remove with args: " + o.toString());
        startTimeOfOperation.put(o + "remove", System.currentTimeMillis());

    }

    @PostRemove
    public void postRemove(Object o) {
        Long time = startTimeOfOperation.remove(o + "remove");
        if (time != null)
            log.info("Method remove with args " + o + " ends for: " + (System.currentTimeMillis() - time) + " ms");

    }

    @PostUpdate
    public void postUpdate(Object o) {
        Long time = startTimeOfOperation.remove(o + "update");
        if (time != null)
            log.info("Method update with args " + o + " ends for: " + (System.currentTimeMillis() - time) + " ms");
    }

    @PostPersist
    public void postPersist(Object o) {
        Long time = startTimeOfOperation.remove(o + "persist");
        if (time != null)
            log.info("Method persist with args " + o + " ends for: " + (System.currentTimeMillis() - time) + " ms");
    }
}
