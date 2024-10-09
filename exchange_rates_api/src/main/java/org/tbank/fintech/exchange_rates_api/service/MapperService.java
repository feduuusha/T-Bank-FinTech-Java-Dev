package org.tbank.fintech.exchange_rates_api.service;

import org.tbank.fintech.exchange_rates_api.model.Currency;

import java.util.List;

public interface MapperService {
    List<Currency> mapXmlToCurrencies(String xml);
    List<String> mapXmlToCodes(String xml);
}
