package org.tbank.fintech.exchange_rates_api.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.tbank.fintech.exchange_rates_api.client.CurrencyRestClient;
import org.tbank.fintech.exchange_rates_api.exception.BadRequestException;
import org.tbank.fintech.exchange_rates_api.model.Currency;
import org.tbank.fintech.exchange_rates_api.model.response.Conversion;
import org.tbank.fintech.exchange_rates_api.model.response.CurrencyRate;
import org.tbank.fintech.exchange_rates_api.service.CurrencyService;
import org.tbank.fintech.exchange_rates_api.service.MapperService;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {

    private final CurrencyRestClient restClient;
    private final MapperService mapperService;

    @Override
    public CurrencyRate findRatesOfCode(String code) {
        if (code.equalsIgnoreCase("RUB")) return new CurrencyRate("RUB", 1D);
        String currenciesXml = restClient.findAllCurrenciesXml(LocalDate.now());
        String currenciesCodes = restClient.findAllCurrenciesCodesXml();
        List<Currency> currencies = mapperService.mapXmlToCurrencies(currenciesXml);
        List<String> codes = mapperService.mapXmlToCodes(currenciesCodes);
        codes
                .stream()
                .filter((obj) -> obj.equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Incorrect currency code: " + code));
        Currency currency = currencies
                .stream()
                .filter(curr -> curr.charCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(code + " code is not included in the current currency quotes"));
        return new CurrencyRate(currency.charCode(), currency.unitRate());
    }

    @Override
    public Conversion convertCurrency(String codeFrom, String codeTo, Double amount) {
        Double rate1 = findRatesOfCode(codeFrom).rate();
        Double rate2 = findRatesOfCode(codeTo).rate();
        return new Conversion(codeFrom.toUpperCase(), codeTo.toUpperCase(), amount * rate1 / rate2);
    }
}
