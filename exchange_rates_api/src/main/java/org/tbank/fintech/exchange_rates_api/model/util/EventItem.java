package org.tbank.fintech.exchange_rates_api.model.util;

import com.fasterxml.jackson.annotation.JsonProperty;

public record EventItem(
        Long id,
        String title,
        String description,
        String price,
        @JsonProperty("site_url")
        String siteUrl
) {
}
