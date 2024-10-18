package org.tbank.fintech.exchange_rates_api.client.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.tbank.fintech.exchange_rates_api.client.EventsRestClient;
import org.tbank.fintech.exchange_rates_api.exception.BadRequestException;
import org.tbank.fintech.exchange_rates_api.exception.UnavailableServiceException;
import org.tbank.fintech.exchange_rates_api.model.util.EventItem;
import org.tbank.fintech.exchange_rates_api.model.util.EventsWrapper;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.Semaphore;

@RequiredArgsConstructor
public class RestClientEventsRestClient implements EventsRestClient {

    private final RestClient restClient;
    private final Semaphore eventsSemaphore;

    @Override
    public List<EventItem> findPopularEventsFromPeriod(long dateFrom, long dateTo, String location, int page, int page_size) {
        try {
            eventsSemaphore.acquire();
            return Objects.requireNonNull(this.restClient
                    .get()
                    .uri(UriComponentsBuilder
                            .fromUriString("/public-api/v1.4/events/")
                            .queryParam("page", page)
                            .queryParam("page_size", page_size)
                            .queryParam("fields", String.join(",", List.of("id", "title", "description", "location", "price", "site_url")))
                            .queryParam("order_by", "price,-favorites_count,-comments_count")
                            .queryParam("text_format", "plain")
                            .queryParam("location", location)
                            .queryParam("actual_since", dateFrom)
                            .queryParam("actual_until", dateTo)
                            .build().toUriString()
                    ).retrieve()
                    .body(EventsWrapper.class)).eventItemList();
        } catch (HttpClientErrorException.NotFound e) {
            throw new NoSuchElementException(e.getMessage());
        } catch (HttpClientErrorException.BadRequest e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            throw new UnavailableServiceException(e.getMessage());
        } finally {
            eventsSemaphore.release();
        }
    }
}
