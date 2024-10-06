package org.tbank.fintech.exchange_rates_api.service;

import org.tbank.fintech.exchange_rates_api.model.response.Conversion;
import org.tbank.fintech.exchange_rates_api.model.response.CurrencyRate;

public interface CurrencyService {
    CurrencyRate findRatesOfCode(String code);
    Conversion convertCurrency(String codeFrom, String codeTo, Double amount);
}
