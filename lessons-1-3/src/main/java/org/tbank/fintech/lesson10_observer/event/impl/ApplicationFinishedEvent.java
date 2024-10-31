package org.tbank.fintech.lesson10_observer.event.impl;

import org.tbank.fintech.lesson10_observer.event.Event;

public class ApplicationFinishedEvent implements Event {

    private static volatile ApplicationFinishedEvent instance;
    private ApplicationFinishedEvent() {}

    public static ApplicationFinishedEvent getInstance() {
        if (instance == null) {
            synchronized (ApplicationFinishedEvent.class) {
                if (instance == null) {
                    instance = new ApplicationFinishedEvent();
                }
            }
        }
        return instance;
    }
    @Override
    public String getType() {
        return "application-finished";
    }
}
