package org.tbank.fintech.lesson10_observer.event_manager.impl;

import lombok.RequiredArgsConstructor;
import org.tbank.fintech.lesson10_observer.event.Event;
import org.tbank.fintech.lesson10_observer.event_manager.EventManager;
import org.tbank.fintech.lesson10_observer.listener.EventListener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class SimpleEventManager implements EventManager {

    private final ConcurrentHashMap<String, List<EventListener>> subscribers;

    @Override
    public void subscribe(Event event, EventListener listener) {
        if (subscribers.containsKey(event.getType())) {
            subscribers.get(event.getType()).add(listener);
        } else {
            var listeners = Collections.synchronizedList(new LinkedList<EventListener>());
            listeners.add(listener);
            subscribers.put(event.getType(), listeners);
        }
    }

    @Override
    public void unsubscribe(Event event, EventListener listener) {
        if (subscribers.containsKey(event.getType())) {
            subscribers.get(event.getType()).remove(listener);
        }
    }

    @Override
    public void notifyAllSubscribersOfEvent(Event event) {
        if (subscribers.containsKey(event.getType())) {
            for (var subscriber : subscribers.get(event.getType())) {
                subscriber.update(event);
            }
        }
    }
}
