package org.tbank.fintech.exchange_rates_api.client.impl;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.tbank.fintech.exchange_rates_api.client.CachedCurrency;
import org.tbank.fintech.exchange_rates_api.client.CurrencyRestClient;
import org.tbank.fintech.exchange_rates_api.exception.UnavailableServiceException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.concurrent.Semaphore;

@RequiredArgsConstructor
@Slf4j
public class CentralBankCurrencyRestClient implements CurrencyRestClient, CachedCurrency {

    private final RestClient restClient;
    private final CacheManager cacheManager;
    private final Semaphore centralBankSemaphore;

    @Override
    @Cacheable("allCurrencies")
    @CircuitBreaker(name = "centralBankAllCurrencies")
    public String findAllCurrenciesXml(LocalDate date) {
        try {
            return receiveAllCurrenciesFromApi(date);
        } catch (HttpClientErrorException e) {
            throw new UnavailableServiceException("Central Bank service is unavailable: " + e.getMessage());
        }
    }

    @Nullable
    private String receiveAllCurrenciesFromApi(LocalDate date) {
        try {
            centralBankSemaphore.acquire();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return this.restClient
                    .get()
                    .uri("/XML_daily.asp?date_req={date_req}", date.format(formatter))
                    .accept(MediaType.APPLICATION_XML)
                    .retrieve()
                    .body(String.class);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        } finally {
            centralBankSemaphore.release();
        }
    }

    @Override
    @Scheduled(fixedRate = 600000)
    public void updateAllCurrenciesXmlCache() {
        try {
            LocalDate date = LocalDate.now();
            Cache currenciesCache = cacheManager.getCache("allCurrencies");
            String xml = receiveAllCurrenciesFromApi(date);
            if (currenciesCache != null) {
                Cache.ValueWrapper wrapper = currenciesCache.get(date);
                if (wrapper != null) {
                    String xmlCache = (String) wrapper.get();
                    if (!Objects.equals(xml, xmlCache)) {
                        currenciesCache.put(date, xml);
                        log.info("allCurrencies cache updated");
                    } else {
                        log.info("allCurrencies cache still relevant");
                    }
                } else {
                    currenciesCache.put(date, xml);
                    log.info("allCurrencies cache updated");
                }
            }
        } catch (Exception e) {
            log.warn("Cant update allCurrencies cache, because: " + e.getMessage());
        }
    }

    @Override
    @Cacheable("currenciesCodes")
    @CircuitBreaker(name = "centralBankCurrenciesCodes")
    public String findAllCurrenciesCodesXml() {
        try {
            return receiveAllCurrenciesCodeXml();
        } catch (HttpClientErrorException e) {
            throw new UnavailableServiceException("Central Bank service is unavailable: " + e.getMessage());
        }
    }

    private String receiveAllCurrenciesCodeXml() {
        try {
            centralBankSemaphore.acquire();
            return this.restClient
                    .get()
                    .uri("/XML_valFull.asp")
                    .accept(MediaType.APPLICATION_XML)
                    .retrieve()
                    .body(String.class);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        } finally {
            centralBankSemaphore.release();
        }
    }

    @Override
    @Scheduled(fixedRate = 600000)
    public void updateAllCurrenciesCodeCache() {
        try {
            Cache currenciesCache = cacheManager.getCache("currenciesCodes");
            String xml = receiveAllCurrenciesCodeXml();
            if (currenciesCache != null) {
                Cache.ValueWrapper wrapper = currenciesCache.get(SimpleKey.EMPTY);
                if (wrapper != null) {
                    String xmlCache = (String) wrapper.get();
                    if (!Objects.equals(xml, xmlCache)) {
                        currenciesCache.put(SimpleKey.EMPTY, xml);
                        log.info("currenciesCodes cache updated");
                    } else {
                        log.info("currenciesCodes cache still relevant");
                    }
                } else {
                    currenciesCache.put(SimpleKey.EMPTY, xml);
                    log.info("currenciesCodes cache updated");
                }
            }
        } catch (Exception e) {
            log.warn("Cant update currenciesCodes cache, because: " + e.getMessage());
        }
    }
}
