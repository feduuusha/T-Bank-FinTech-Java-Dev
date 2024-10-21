package org.tbank.fintech.exchange_rates_api.service;



import org.tbank.fintech.exchange_rates_api.model.Event;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface EventService {
    CompletableFuture<List<Event>> findPopularEventsFromPeriod(Integer budget, String currency, Date dateFrom, Date dateTo, String location, int page, int pageSize);
}
