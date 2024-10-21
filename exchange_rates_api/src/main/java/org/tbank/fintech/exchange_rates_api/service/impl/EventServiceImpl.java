package org.tbank.fintech.exchange_rates_api.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.tbank.fintech.exchange_rates_api.client.EventsRestClient;
import org.tbank.fintech.exchange_rates_api.exception.BadRequestException;
import org.tbank.fintech.exchange_rates_api.exception.UnavailableServiceException;
import org.tbank.fintech.exchange_rates_api.model.Event;
import org.tbank.fintech.exchange_rates_api.model.util.EventItem;
import org.tbank.fintech.exchange_rates_api.service.CurrencyService;
import org.tbank.fintech.exchange_rates_api.service.EventService;
import org.tbank.fintech.exchange_rates_api.util.WeeksCalendar;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventsRestClient eventsRestClient;
    private final CurrencyService currencyService;
    private final WeeksCalendar weeksCalendar;
    private final Function<Throwable, List<Event>> eventServiceExceptionHandler;

//    Метод для задания 2 на основе completableFuture
    @Override
    public CompletableFuture<List<Event>> findPopularEventsFromPeriod(Integer budget, String currency, Date dateFrom, Date dateTo, String location, int page, int pageSize) {
        long dateFromUnix;
        long dateToUnix;
        if (dateFrom != null && dateTo != null) {
            dateFromUnix = dateFrom.toInstant().getEpochSecond();
            dateToUnix = dateTo.toInstant().getEpochSecond();
        } else {
            dateFromUnix = weeksCalendar.getStartOfWeekTimeStamp();
            dateToUnix = weeksCalendar.getEndOfWeekTimeStamp();
        }
        CompletableFuture<List<EventItem>> eventsFuture = CompletableFuture.supplyAsync(() -> this.eventsRestClient.findPopularEventsFromPeriod(dateFromUnix, dateToUnix, location, page, pageSize));
        CompletableFuture<Integer> convertedBudgetFuture = CompletableFuture.supplyAsync(() -> this.currencyService.convertCurrency(currency, "RUB", budget.doubleValue()).convertedAmount().intValue());
        Pattern pattern = Pattern.compile("\\d+");
        return eventsFuture
                .thenCombine(convertedBudgetFuture, (eventsFromApi, convertedBudget) ->
                        eventsFromApi
                                .stream()
                                .map((obj) -> {
                                    Matcher matcher = pattern.matcher(obj.price());
                                    long price;
                                    if (matcher.find()) {
                                        price = Long.parseLong(matcher.group());
                                    } else {
                                        price = 0L;
                                    }
                                    return new Event(obj.id(), obj.title(), obj.description(), price, location, obj.siteUrl());
                                })
                                .sorted((Comparator.comparing(Event::price)))
                                .filter((obj) -> obj.price() <= convertedBudget)
                                .toList())
                .completeOnTimeout(List.of(), 1, TimeUnit.MINUTES)
                .exceptionally(eventServiceExceptionHandler);
    }

//    Метод для задания 3 основан на элементах project reactor mono и flux
    public Mono<List<Event>> findEvents(long dateFromUnix, long dateToUnix, String location, int page, int pageSize, String currency, int budget) {
        return Mono.zip(
                        Mono.fromCallable(
                                        () -> eventsRestClient.findPopularEventsFromPeriod(dateFromUnix, dateToUnix, location, page, pageSize))
                                .subscribeOn(Schedulers.parallel()).log(),
                        Mono.fromCallable(
                                        () -> currencyService.convertCurrency(currency, "RUB", Integer.valueOf(budget).doubleValue()).convertedAmount().intValue())
                                .subscribeOn(Schedulers.parallel()).log()
                )
                .timeout(Duration.ofMinutes(1))
                .retry(2)
                .log()
                .flatMap(tuple -> {
                    List<EventItem> eventsFromApi = tuple.getT1();
                    int convertedBudget = tuple.getT2();

                    Pattern pattern = Pattern.compile("\\d+");
                    return Flux.fromIterable(eventsFromApi)
                            .map(obj -> {
                                Matcher matcher = pattern.matcher(obj.price());
                                long price = matcher.find() ? Long.parseLong(matcher.group()) : 0L;
                                return new Event(obj.id(), obj.title(), obj.description(), price, location, obj.siteUrl());
                            })
                            .sort(Comparator.comparing(Event::price))
                            .filter(obj -> obj.price() <= convertedBudget)
                            .collectList();
                })
                .onErrorMap(throwable -> {
                    if (throwable instanceof HttpClientErrorException.BadRequest e) {
                        throw new BadRequestException(e.getMessage());
                    } else if (throwable instanceof BadRequestException e) {
                        throw e;
                    } else if (throwable instanceof HttpClientErrorException.NotFound e) {
                        throw new NoSuchElementException(e.getMessage());
                    } else if (throwable instanceof NoSuchElementException e) {
                        throw e;
                    } else if (throwable instanceof HttpServerErrorException e) {
                        throw new UnavailableServiceException(e.getMessage());
                    } else if (throwable instanceof TimeoutException e) {
                        throw new UnavailableServiceException(e.getMessage());
                    } else {
                        throw new IllegalStateException(throwable.getMessage());
                    }
                });
    }
}

