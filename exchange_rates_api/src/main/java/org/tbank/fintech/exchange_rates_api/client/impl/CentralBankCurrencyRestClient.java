package org.tbank.fintech.exchange_rates_api.client.impl;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.tbank.fintech.exchange_rates_api.client.CurrencyRestClient;
import org.tbank.fintech.exchange_rates_api.exception.UnavailableServiceException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
public class CentralBankCurrencyRestClient implements CurrencyRestClient {

    private final RestClient restClient;

    @Override
    @Cacheable("allCurrencies")
    @CircuitBreaker(name = "centralBankAllCurrencies")
    public String findAllCurrenciesXml(LocalDate date) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return this.restClient
                    .get()
                    .uri("/XML_daily.asp?date_req={date_req}", date.format(formatter))
                    .accept(MediaType.APPLICATION_XML)
                    .retrieve()
                    .body(String.class);
        } catch (HttpClientErrorException e) {
            throw new UnavailableServiceException("Central Bank service is unavailable: " + e.getMessage());
        }
    }

    @Override
    @Cacheable("currenciesCodes")
    @CircuitBreaker(name = "centralBankCurrenciesCodes")
    public String findAllCurrenciesCodesXml() {
        try {
            return this.restClient
                    .get()
                    .uri("/XML_valFull.asp")
                    .accept(MediaType.APPLICATION_XML)
                    .retrieve()
                    .body(String.class);
        } catch (HttpClientErrorException e) {
            throw new UnavailableServiceException("Central Bank service is unavailable: " + e.getMessage());
        }
    }
}
