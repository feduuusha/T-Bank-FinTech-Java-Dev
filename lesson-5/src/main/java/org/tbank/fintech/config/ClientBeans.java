package org.tbank.fintech.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.tbank.fintech.clients.impl.RestClientCategoriesRestClient;
import org.tbank.fintech.clients.impl.RestClientLocationsRestClient;

@Configuration
public class ClientBeans {

    @Bean
    public RestClientCategoriesRestClient categoriesRestClient(
            @Value("${categories.baseurl:https://kudago.com}") String baseUrl
    ) {
        return new RestClientCategoriesRestClient(
                RestClient
                        .builder()
                        .baseUrl(baseUrl)
                        .build()
        );
    }

    @Bean
    public RestClientLocationsRestClient locationsRestClient(
            @Value("${locations.baseurl:https://kudago.com}") String baseUrl
    ) {
        return new RestClientLocationsRestClient(
                RestClient
                        .builder()
                        .baseUrl(baseUrl)
                        .build()
        );
    }
}
