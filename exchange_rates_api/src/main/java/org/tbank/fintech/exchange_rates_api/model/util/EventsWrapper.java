package org.tbank.fintech.exchange_rates_api.model.util;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record EventsWrapper (
        @JsonProperty("results")
        List<EventItem> eventItemList
) {

}
