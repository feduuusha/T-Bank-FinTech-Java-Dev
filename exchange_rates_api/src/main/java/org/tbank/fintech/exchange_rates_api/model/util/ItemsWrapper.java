package org.tbank.fintech.exchange_rates_api.model.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement(localName = "Valuta")
public record ItemsWrapper(
        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "Item")
        List<Item> items
) {
}
