package org.tbank.fintech.exchange_rates_api.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.tbank.fintech.exchange_rates_api.client.EventsRestClient;
import org.tbank.fintech.exchange_rates_api.exception.BadRequestException;
import org.tbank.fintech.exchange_rates_api.exception.UnavailableServiceException;
import org.tbank.fintech.exchange_rates_api.model.Event;
import org.tbank.fintech.exchange_rates_api.model.response.Conversion;
import org.tbank.fintech.exchange_rates_api.model.util.EventItem;
import org.tbank.fintech.exchange_rates_api.service.impl.EventServiceImpl;
import org.tbank.fintech.exchange_rates_api.util.WeeksCalendar;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletionException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

/**
 * Test class for {@link org.tbank.fintech.exchange_rates_api.service.impl.EventServiceImpl}
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {EventServiceImpl.class})
public class EventServiceImplTests {
    @Autowired
    private EventService eventService;
    @MockBean
    private CurrencyService currencyService;
    @MockBean
    private EventsRestClient eventsRestClient;
    @MockBean
    private WeeksCalendar weeksCalendar;

    @Test
    @DisplayName("Method findPopularEventsFromPeriod should work correct, because clients work correct and period provided")
    public void findPopularEventsFromPeriodPeriodProvidedSuccessTest() {
        // Arrange
        int budget = 1;
        String currency = "USD";
        Date dateFrom = new Date(100);
        Date dateTo = new Date(200);
        String location = "kzn";
        int page = 1;
        int pageSize = 20;
        Conversion conversionFromCurrencyService = new Conversion(currency, "RUB", 100D);
        List<EventItem> eventsFromApi = List.of(
                new EventItem(1L, "title1", "desc1", "100","siteUrl1"),
                new EventItem(2L, "title2", "desc2", "20", "siteUrl2"),
                new EventItem(3L, "title3", "desc3", "beleberda", "siteUrl3")
        );
        when(this.eventsRestClient.findPopularEventsFromPeriod(dateFrom.toInstant().getEpochSecond(), dateTo.toInstant().getEpochSecond(), location, page, pageSize))
                .thenReturn(eventsFromApi);
        when(this.currencyService.convertCurrency(currency, "RUB", Integer.valueOf(budget).doubleValue()))
                .thenReturn(conversionFromCurrencyService);
        List<Event> expectedEvents = List.of(
                new Event(3L, "title3", "desc3", 0L, "kzn", "siteUrl3"),
                new Event(2L, "title2", "desc2", 20L, "kzn", "siteUrl2"),
                new Event(1L, "title1", "desc1", 100L, "kzn", "siteUrl1")
        );

        // Act
        List<Event> events = eventService.findPopularEventsFromPeriod(budget, currency, dateFrom, dateTo,
                location, page, pageSize).join();

        // Assert
        assertThat(events).isEqualTo(expectedEvents);
    }

    @Test
    @DisplayName("Method findPopularEventsFromPeriod should work correct, because clients work correct and period is not provided")
    public void findPopularEventsFromPeriodPeriodIsNotProvidedSuccessTest() {
        // Arrange
        int budget = 1;
        String currency = "USD";
        long startOfWeek = 500;
        long endOfWeek = 600;
        String location = "kzn";
        int page = 1;
        int pageSize = 20;
        Conversion conversionFromCurrencyService = new Conversion(currency, "RUB", 100D);
        List<EventItem> eventsFromApi = List.of(
                new EventItem(1L, "title1", "desc1", "100","siteUrl1"),
                new EventItem(2L, "title2", "desc2", "20", "siteUrl2"),
                new EventItem(3L, "title3", "desc3", "30", "siteUrl3")
        );
        when(weeksCalendar.getStartOfWeekTimeStamp()).thenReturn(startOfWeek);
        when(weeksCalendar.getEndOfWeekTimeStamp()).thenReturn(endOfWeek);
        when(this.eventsRestClient.findPopularEventsFromPeriod(startOfWeek, endOfWeek, location, page, pageSize))
                .thenReturn(eventsFromApi);
        when(this.currencyService.convertCurrency(currency, "RUB", Integer.valueOf(budget).doubleValue()))
                .thenReturn(conversionFromCurrencyService);
        List<Event> expectedEvents = List.of(
                new Event(2L, "title2", "desc2", 20L, "kzn", "siteUrl2"),
                new Event(3L, "title3", "desc3", 30L, "kzn", "siteUrl3"),
                new Event(1L, "title1", "desc1", 100L, "kzn", "siteUrl1")
        );

        // Act
        List<Event> events = eventService.findPopularEventsFromPeriod(budget, currency, null, null,
                location, page, pageSize).join();

        // Assert
        assertThat(events).isEqualTo(expectedEvents);
    }

    @ParameterizedTest
    @MethodSource("dataForFindPopularEventsFromPeriodUnSuccessTests")
    @DisplayName("Method findPopularEventsFromPeriod should catch some exception and re-throw other exception")
    public void findPopularEventsFromPeriodUnSuccessTest(Class<? extends Throwable> thrownException, Class<? extends Throwable> expectedException) {
        // Arrange
        Date dateFrom = new Date(100);
        Date dateTo = new Date(200);
        when(eventsRestClient.findPopularEventsFromPeriod(dateFrom.toInstant().getEpochSecond(), dateTo.toInstant().getEpochSecond(), "", 1, 1)).thenThrow(thrownException);

        // Act
        // Assert
        assertThatExceptionOfType(expectedException)
                .isThrownBy(() -> {
                    try {
                        eventService.findPopularEventsFromPeriod(1, "", dateFrom, dateTo, "", 1, 1).join();
                    } catch (CompletionException exception) {
                        throw exception.getCause();
                    }
        });
    }


    static Stream<Arguments> dataForFindPopularEventsFromPeriodUnSuccessTests() {
        return Stream.of(
                Arguments.arguments(HttpClientErrorException.BadRequest.class, BadRequestException.class),
                Arguments.arguments(BadRequestException.class, BadRequestException.class),
                Arguments.arguments(HttpClientErrorException.NotFound.class, NoSuchElementException.class),
                Arguments.arguments(NoSuchElementException.class, NoSuchElementException.class),
                Arguments.arguments(HttpServerErrorException.class, UnavailableServiceException.class),
                Arguments.arguments(RuntimeException.class, IllegalStateException.class)
        );
    }
}
