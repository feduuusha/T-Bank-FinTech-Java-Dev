package org.tbank.fintech.lesson10_observer.listener.impl;

import lombok.Getter;
import org.tbank.fintech.lesson10_observer.event.Event;
import org.tbank.fintech.lesson10_observer.event.impl.HttpRequestEvent;
import org.tbank.fintech.lesson10_observer.event.impl.HttpsRequestEvent;
import org.tbank.fintech.lesson10_observer.listener.EventListener;

import java.util.concurrent.atomic.AtomicLong;

@Getter
public class WebEventListener implements EventListener {
    private final AtomicLong countOfSecuredRequests = new AtomicLong(0);
    private final AtomicLong countOfNonSecuredRequests = new AtomicLong(0);
    @Override
    public void update(Event event) {
        if (event instanceof HttpRequestEvent) {
            countOfSecuredRequests.incrementAndGet();
        } else if (event instanceof HttpsRequestEvent) {
            countOfNonSecuredRequests.incrementAndGet();
        }
    }
}
