package org.tbank.fintech.lesson10_observer.event_manager;

import org.tbank.fintech.lesson10_observer.event.Event;
import org.tbank.fintech.lesson10_observer.listener.EventListener;

public interface EventManager {
    void subscribe(Event event, EventListener listener);
    void unsubscribe(Event event, EventListener listener);
    void notifyAllSubscribersOfEvent(Event event);
}
