package org.tbank.fintech.exchange_rates_api.client;

import java.time.LocalDate;

public interface CurrencyRestClient {

    String findAllCurrenciesXml(LocalDate date);
    String findAllCurrenciesCodesXml();

}
