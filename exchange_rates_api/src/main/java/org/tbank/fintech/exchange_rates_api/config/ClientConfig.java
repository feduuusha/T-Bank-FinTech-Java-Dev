package org.tbank.fintech.exchange_rates_api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.tbank.fintech.exchange_rates_api.client.impl.CentralBankCurrencyRestClient;

@Configuration
public class ClientConfig {

    @Bean
    public CentralBankCurrencyRestClient currencyRestClient(
            @Value("${clients.currency.url:www.cbr.ru/scripts}") String url
    ) {
       return new CentralBankCurrencyRestClient(
                RestClient
                        .builder()
                        .baseUrl(url)
                        .build()
        );
    }
}
