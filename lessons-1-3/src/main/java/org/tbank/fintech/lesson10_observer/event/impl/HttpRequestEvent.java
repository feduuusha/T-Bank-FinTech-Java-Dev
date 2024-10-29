package org.tbank.fintech.lesson10_observer.event.impl;

import org.tbank.fintech.lesson10_observer.event.Event;

public class HttpRequestEvent implements Event {

    private static volatile HttpRequestEvent instance;
    private HttpRequestEvent() {}

    public static HttpRequestEvent getInstance() {
        if (instance == null) {
            synchronized (HttpRequestEvent.class) {
                if (instance == null) {
                    instance = new HttpRequestEvent();
                }
            }
        }
        return instance;
    }

    @Override
    public String getType() {
        return "http-request";
    }
}
