package org.tbank.fintech.exchange_rates_api.client;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.tbank.fintech.exchange_rates_api.client.impl.RestClientEventsRestClient;
import org.tbank.fintech.exchange_rates_api.config.CacheConfig;
import org.tbank.fintech.exchange_rates_api.config.ClientConfig;
import org.tbank.fintech.exchange_rates_api.exception.BadRequestException;
import org.tbank.fintech.exchange_rates_api.exception.UnavailableServiceException;
import org.tbank.fintech.exchange_rates_api.model.util.EventItem;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wiremock.integrations.testcontainers.WireMockContainer;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * Test class for the {@link RestClientEventsRestClient}
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {ClientConfig.class, CacheConfig.class})
@Testcontainers(disabledWithoutDocker = true)
public class RestClientEventsRestClientTests {

    @Autowired
    private EventsRestClient eventsRestClient;

    @Container
    static WireMockContainer wireMockContainer = new WireMockContainer("wiremock/wiremock:3.6.0")
            .withMappingFromResource(RestClientEventsRestClientTests.class, "mocks-config.json");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry propertyRegistry) {
        propertyRegistry.add("clients.events.url", wireMockContainer::getBaseUrl);
    }

    @Test
    @DisplayName("Method findPopularEventsFromPeriod should return correct json, because request is correct")
    public void findPopularEventsFromPeriodSuccessesTest() {
        // Arrange
        long dateFrom = 1726434000L;
        long dateTo = 1726693199L;
        String location = "kzn";
        int page = 1;
        int pageSize = 2;
        List<EventItem> eventsInApi = List.of(
                new EventItem(211101L, "Выставка «Казанское Поволжье: образы народной культуры»", "Проект предлагает уникальный взгляд на традиции народов, живущих на территории Казанского Поволжья во второй половине XIX — начале XX века.\n", "", "https://kzn.kudago.com/event/vyistavka-kazanskoe-povolzhe-obrazyi-narodnoj-kulturyi-sentyabr-2024/"),
                new EventItem(133024L, "выставка «Места обетованные»", "Посетители выставки познакомятся с двадцатью репродукциями печатных шамаилей из коллекции Фонда Марджани, созданных в начале двадцатого столетия.\n\n", "", "https://kzn.kudago.com/event/vystavka-mesta-obetovannye/")
        );

        // Act
        List<EventItem> events = this.eventsRestClient.findPopularEventsFromPeriod(dateFrom, dateTo, location, page, pageSize);

        // Assert
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(events.size()).isEqualTo(eventsInApi.size());
        for (int i = 0; i < events.size(); ++i) {
            softly.assertThat(events.get(i)).isEqualTo(eventsInApi.get(i));
        }

        softly.assertAll();
    }

    @ParameterizedTest
    @MethodSource("dataForUnSuccessesTests")
    @DisplayName("Method findPopularEventsFromPeriod should throw some exception, because request is incorrect")
    public void findPopularEventsFromPeriodUnSuccessesParameterizedTest(String location, Class<? extends Throwable> exception, String exceptionMessage) {
        // Arrange
        long dateFrom = 1726434000L;
        long dateTo = 1726693199L;
        int page = 1;
        int pageSize = 2;
        // Act
        // Assert
        assertThatExceptionOfType(exception)
                .isThrownBy(() -> eventsRestClient.findPopularEventsFromPeriod(dateFrom, dateTo, location, page, pageSize))
                .withMessage(exceptionMessage);

    }

    static Stream<Arguments> dataForUnSuccessesTests() {
        return Stream.of(
                Arguments.arguments("randomlocations", BadRequestException.class, "400 Bad Request: [no body]"),
                Arguments.arguments("uganda", NoSuchElementException.class, "404 Not Found: [no body]"),
                Arguments.arguments("null", UnavailableServiceException.class, "503 Service Unavailable: [no body]")
        );
    }
}
