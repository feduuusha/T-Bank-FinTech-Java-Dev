package org.tbank.fintech.clients;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.tbank.fintech.config.ClientBeans;
import org.tbank.fintech.entity.Coords;
import org.tbank.fintech.entity.Location;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wiremock.integrations.testcontainers.WireMockContainer;

import java.util.List;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

@SpringBootTest(webEnvironment = NONE, classes = {ClientBeans.class})
@Testcontainers
public class LocationsRestClientTest {

    @Autowired
    private LocationsRestClient locationsRestClient;

    @Container
    static WireMockContainer wiremockServer = new WireMockContainer("wiremock/wiremock:3.6.0")
            .withMappingFromResource(LocationsRestClientTest.class,"mocks-config.json");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("locations.baseurl", wiremockServer::getBaseUrl);
    }

    @Test
    public void findAllLocations_apiProvidesThreeLocations_shouldReturnListOfThreeLocations() {
        // Arrange
        String lang = "ru";
        String orderBy = "slag";
        List<String> fields = List.of("slug", "name", "timezone", "coords", "language");
        List<Location> apiLocations = List.of(
                new Location("ekb", "Екатеринбург", "Asia/Yekaterinburg", new Coords(56.838606999999996, 60.60551400000001), "ru"),
                new Location("kzn", "Казань", "GMT+03:00", new Coords(55.795792999999975, 49.106584999999995), "ru"),
                new Location("msk", "Москва", "GMT+03:00", new Coords(55.753676, 37.61989899999998), "ru")
        );

        // Act
        List<Location> locations = locationsRestClient.findAllLocations(lang, orderBy, fields);

        // Assert
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(locations.size()).isEqualTo(apiLocations.size());
        for (int i = 0; i < apiLocations.size(); ++i) {
            softly.assertThat(locations.get(i)).isEqualTo(apiLocations.get(i));
        }

        softly.assertAll();
    }
}
