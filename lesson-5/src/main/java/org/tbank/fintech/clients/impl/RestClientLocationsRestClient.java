package org.tbank.fintech.clients.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;
import org.tbank.fintech.clients.LocationsRestClient;
import org.tbank.fintech.entity.Location;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class RestClientLocationsRestClient implements LocationsRestClient {

    private static final ParameterizedTypeReference<List<Location>> LOCATIONS_TYPE_REFERENCE = new ParameterizedTypeReference<>() {
    };

    private final RestClient restClient;

    @Override
    public List<Location> findAllLocations(String lang, String orderBy, List<String> fields) {
        return restClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/public-api/v1.4/locations/")
                        .queryParam("lang", lang)
                        .queryParam("order_by", orderBy)
                        .queryParam("fields", String.join(",", fields))
                        .build())
                .retrieve()
                .body(LOCATIONS_TYPE_REFERENCE);
    }
}
