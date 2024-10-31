package org.tbank.fintech.lesson10_observer;

import org.tbank.fintech.lesson10_observer.event.impl.ApplicationFinishedEvent;
import org.tbank.fintech.lesson10_observer.event.impl.ApplicationStartedEvent;
import org.tbank.fintech.lesson10_observer.event.impl.HttpRequestEvent;
import org.tbank.fintech.lesson10_observer.event.impl.HttpsRequestEvent;
import org.tbank.fintech.lesson10_observer.event_manager.impl.SimpleEventManager;
import org.tbank.fintech.lesson10_observer.listener.impl.LoggingEventListener;
import org.tbank.fintech.lesson10_observer.listener.impl.WebEventListener;

import java.util.concurrent.ConcurrentHashMap;

public class Main {
    public static void main(String[] args) {
        var eventManager = new SimpleEventManager(new ConcurrentHashMap<>());
        var loggingListener = new LoggingEventListener();
        var webListener = new WebEventListener();

        eventManager.subscribe(ApplicationStartedEvent.getInstance(), loggingListener);
        eventManager.subscribe(ApplicationFinishedEvent.getInstance(), loggingListener);
        eventManager.subscribe(HttpsRequestEvent.getInstance(), loggingListener);
        eventManager.subscribe(HttpRequestEvent.getInstance(), loggingListener);
        eventManager.subscribe(HttpRequestEvent.getInstance(), webListener);
        eventManager.subscribe(HttpsRequestEvent.getInstance(), webListener);

        var app = new Application(eventManager);
        app.run();

        System.out.println("Count of non secured requests: " + webListener.getCountOfNonSecuredRequests());
        System.out.println("Count of secured requests: " + webListener.getCountOfSecuredRequests());


        eventManager.unsubscribe(ApplicationStartedEvent.getInstance(), loggingListener);
        eventManager.unsubscribe(ApplicationFinishedEvent.getInstance(), loggingListener);
        eventManager.unsubscribe(HttpsRequestEvent.getInstance(), loggingListener);
        eventManager.unsubscribe(HttpRequestEvent.getInstance(), loggingListener);
        eventManager.unsubscribe(HttpRequestEvent.getInstance(), webListener);
        eventManager.unsubscribe(HttpsRequestEvent.getInstance(), webListener);
    }
}
