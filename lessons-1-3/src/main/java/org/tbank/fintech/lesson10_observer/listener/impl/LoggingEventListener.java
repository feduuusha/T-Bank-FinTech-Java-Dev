package org.tbank.fintech.lesson10_observer.listener.impl;

import lombok.extern.slf4j.Slf4j;
import org.tbank.fintech.lesson10_observer.event.Event;
import org.tbank.fintech.lesson10_observer.listener.EventListener;

@Slf4j
public class LoggingEventListener implements EventListener {
    @Override
    public void update(Event event) {
        switch (event.getType()) {
            case "application-started" -> log.info("Application started");
            case "application-finished" -> log.info("Application finished");
            case "http-request" -> log.info("Http request processed");
            case "https-request" -> log.info("Https request processed");
        }
    }
}
