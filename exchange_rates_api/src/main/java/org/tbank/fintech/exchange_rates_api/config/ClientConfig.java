package org.tbank.fintech.exchange_rates_api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.tbank.fintech.exchange_rates_api.client.impl.CentralBankCurrencyRestClient;
import org.tbank.fintech.exchange_rates_api.client.impl.RestClientEventsRestClient;

import java.util.concurrent.Semaphore;

@Configuration
public class ClientConfig {

    @Bean
    public CentralBankCurrencyRestClient currencyRestClient(
            @Value("${clients.currency.url:www.cbr.ru/scripts}") String url,
            @Autowired CacheManager cacheManager,
            @Autowired Semaphore centralBankSemaphore
    ) {
       return new CentralBankCurrencyRestClient(
                RestClient
                        .builder()
                        .baseUrl(url)
                        .build(),
               cacheManager,
               centralBankSemaphore);
    }

    @Bean
    public RestClientEventsRestClient eventsRestClient(
            @Value("${clients.events.url:https://kudago.com}") String url,
            @Autowired Semaphore eventsSemaphore
    ) {
        return new RestClientEventsRestClient(
                RestClient
                        .builder()
                        .baseUrl(url)
                        .build(),
                eventsSemaphore
        );
    }

    @Bean
    public Semaphore centralBankSemaphore(
            @Value("${clients.currency.number-connection-at-same-time}") int numberOfConnections
    ) {
        return new Semaphore(numberOfConnections, true);
    }

    @Bean
    public Semaphore eventsSemaphore(
            @Value("${clients.events.number-connection-at-same-time}") int numberOfConnections
    ) {
        return new Semaphore(numberOfConnections, true);
    }
}
