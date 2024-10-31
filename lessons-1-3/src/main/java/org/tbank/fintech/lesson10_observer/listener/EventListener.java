package org.tbank.fintech.lesson10_observer.listener;

import org.tbank.fintech.lesson10_observer.event.Event;

public interface EventListener {
    void update(Event event);
}
