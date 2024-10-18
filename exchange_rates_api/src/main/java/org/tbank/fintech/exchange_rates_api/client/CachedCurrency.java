package org.tbank.fintech.exchange_rates_api.client;

public interface CachedCurrency {
    void updateAllCurrenciesXmlCache();
    void updateAllCurrenciesCodeCache();
}
