package org.tbank.fintech.lesson10_observer.event.impl;

import org.tbank.fintech.lesson10_observer.event.Event;

public class HttpsRequestEvent implements Event {

    private static volatile HttpsRequestEvent instance;
    private HttpsRequestEvent() {}

    public static HttpsRequestEvent getInstance() {
        if (instance == null) {
            synchronized (HttpsRequestEvent.class) {
                if (instance == null) {
                    instance = new HttpsRequestEvent();
                }
            }
        }
        return instance;
    }
    @Override
    public String getType() {
        return "https-request";
    }
}
