package org.tbank.fintech.exchange_rates_api.client;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.tbank.fintech.exchange_rates_api.config.CacheConfig;
import org.tbank.fintech.exchange_rates_api.config.ClientConfig;

import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

@SpringBootTest(classes = {ClientConfig.class, CacheConfig.class})
public class SemaphoreTests {
    @Autowired
    private EventsRestClient eventsRestClient;
    @Autowired
    private Semaphore eventsSemaphore;
    @Value("${clients.events.number-connection-at-same-time}")
    private int eventConnections;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry propertyRegistry) {
        propertyRegistry.add("clients.events.url", () -> "random");
    }
    @Test
    @DisplayName("If a lot of threads call eventRestController method eventsSemaphore.availablePermits() should be zero and after time it should be like in properties")
    public void eventsSemaphoreTest() throws Exception {
        // Arrange
        boolean isFull = false;
        try (var executor =  Executors.newFixedThreadPool(eventConnections)) {
            // Act
            for (int i = 0; i < eventConnections + 1000; ++i) {
                executor.submit(() -> eventsRestClient.findPopularEventsFromPeriod(0, 0, null, 0, 0));
                if (eventsSemaphore.availablePermits() == 0) isFull = true;
            }

            // Assert
            SoftAssertions softly = new SoftAssertions();

            softly.assertThat(isFull).isTrue();
            Thread.sleep(1000);
            softly.assertThat(eventsSemaphore.availablePermits()).isEqualTo(eventConnections);

            softly.assertAll();
        }
    }
}
