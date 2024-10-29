package org.tbank.fintech.lesson10_observer;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.tbank.fintech.lesson10_observer.event.impl.ApplicationFinishedEvent;
import org.tbank.fintech.lesson10_observer.event.impl.ApplicationStartedEvent;
import org.tbank.fintech.lesson10_observer.event.impl.HttpRequestEvent;
import org.tbank.fintech.lesson10_observer.event.impl.HttpsRequestEvent;
import org.tbank.fintech.lesson10_observer.event_manager.EventManager;

import java.util.Random;

@RequiredArgsConstructor
public class Application {

    private final EventManager eventManager;

//    Имитация работы приложения и происходящих в нем ивентов
    @SneakyThrows
    @SuppressWarnings("BusyWait")
    public void run() {
        eventManager.notifyAllSubscribersOfEvent(ApplicationStartedEvent.getInstance());


        long start = System.currentTimeMillis();
        Random random = new Random();
        // Работает на протяжении 30 секунд
        while (System.currentTimeMillis() < start + 30000) {
            if (random.nextBoolean()) {
                eventManager.notifyAllSubscribersOfEvent(HttpsRequestEvent.getInstance());
            } else {
                eventManager.notifyAllSubscribersOfEvent(HttpRequestEvent.getInstance());
            }
            Thread.sleep(1000);
        }


        eventManager.notifyAllSubscribersOfEvent(ApplicationFinishedEvent.getInstance());
    }
}
