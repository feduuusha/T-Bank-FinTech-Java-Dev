package org.tbank.fintech.exchange_rates_api.client;

import org.tbank.fintech.exchange_rates_api.model.util.EventItem;

import java.util.List;

public interface EventsRestClient {

    List<EventItem> findPopularEventsFromPeriod(long dateFrom, long dateTo, String location, int page, int page_size);
}
