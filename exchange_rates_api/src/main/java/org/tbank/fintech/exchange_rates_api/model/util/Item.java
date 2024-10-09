package org.tbank.fintech.exchange_rates_api.model.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Item (
        @JacksonXmlProperty(localName = "ISO_Char_Code")
        String code
) {
}
