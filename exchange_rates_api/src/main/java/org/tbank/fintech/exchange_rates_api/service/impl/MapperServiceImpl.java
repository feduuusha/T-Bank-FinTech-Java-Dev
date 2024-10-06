package org.tbank.fintech.exchange_rates_api.service.impl;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.tbank.fintech.exchange_rates_api.model.Currency;
import org.tbank.fintech.exchange_rates_api.model.util.Item;
import org.tbank.fintech.exchange_rates_api.model.util.ItemsWrapper;
import org.tbank.fintech.exchange_rates_api.model.util.ValutesWrapper;
import org.tbank.fintech.exchange_rates_api.service.MapperService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MapperServiceImpl implements MapperService {

    private final XmlMapper xmlMapper;

    @Override
    public List<Currency> mapXmlToCurrencies(String xml) {
        try {
            return xmlMapper.readValue(xml, ValutesWrapper.class)
                    .valutes()
                    .stream()
                    .map((obj) ->
                            new Currency(
                                    obj.charCode(),
                                    Double.valueOf(obj.unitRate().replace(",", "."))
                            ))
                    .toList();
        } catch (Exception e) {
            throw new IllegalStateException("External service has changed its API");
        }
    }

    @Override
    public List<String> mapXmlToCodes(String xml) {
        try {
            return xmlMapper.readValue(xml, ItemsWrapper.class)
                    .items()
                    .stream()
                    .map(Item::code)
                    .toList();
        } catch (Exception e) {
            throw new IllegalStateException("External service has changed its API");
        }
    }
}
