package org.tbank.fintech.clients.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;
import org.tbank.fintech.clients.CategoriesRestClient;
import org.tbank.fintech.entity.Category;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class RestClientCategoriesRestClient implements CategoriesRestClient {

    private static final ParameterizedTypeReference<List<Category>> CATEGORIES_TYPE_REFERENCE = new ParameterizedTypeReference<>() {
    };

    private final RestClient restClient;

    @Override
    public List<Category> findAllCategories(String lang, String orderBy, List<String> fields) {
        return restClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/public-api/v1.4/event-categories/")
                        .queryParam("lang", lang)
                        .queryParam("order_by", orderBy)
                        .queryParam("fields", String.join(",", fields))
                        .build())
                .retrieve()
                .body(CATEGORIES_TYPE_REFERENCE);
    }
}
