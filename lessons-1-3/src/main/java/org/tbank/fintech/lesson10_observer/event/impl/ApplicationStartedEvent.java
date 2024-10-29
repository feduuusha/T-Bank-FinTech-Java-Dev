package org.tbank.fintech.lesson10_observer.event.impl;

import org.tbank.fintech.lesson10_observer.event.Event;

public class ApplicationStartedEvent implements Event {

    private static volatile ApplicationStartedEvent instance;
    private ApplicationStartedEvent() {}

    public static ApplicationStartedEvent getInstance() {
        if (instance == null) {
            synchronized (ApplicationStartedEvent.class) {
                if (instance == null) {
                    instance = new ApplicationStartedEvent();
                }
            }
        }
        return instance;
    }
    @Override
    public String getType() {
        return "application-started";
    }
}
