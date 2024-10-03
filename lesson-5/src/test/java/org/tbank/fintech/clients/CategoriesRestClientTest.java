package org.tbank.fintech.clients;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.tbank.fintech.config.ClientBeans;
import org.tbank.fintech.entity.Category;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wiremock.integrations.testcontainers.WireMockContainer;

import java.util.List;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

@SpringBootTest(webEnvironment = NONE, classes = {ClientBeans.class})
@Testcontainers
public class CategoriesRestClientTest {

    @Autowired
    private CategoriesRestClient categoriesRestClient;

    @Container
    static WireMockContainer wiremockServer = new WireMockContainer("wiremock/wiremock:3.6.0")
            .withMappingFromResource(CategoriesRestClientTest.class,"mocks-config.json");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("categories.baseurl", wiremockServer::getBaseUrl);
    }

    @Test
    public void findAllCategoriesTest_apiProvidesTwoCategories_shouldReturnListOfTwoCategories() {
        // Arrange
        String lang = "ru";
        String orderBy = "slug";
        List<String> fields = List.of("id", "slug", "name");
        List<Category> apiCategories = List.of(
                new Category(1L, "academy-of-music", "Музыкальные школы (Учебн завед)"),
                new Category(2L, "airports", "Аэропорты")
        );

        // Act
        List<Category> categories = categoriesRestClient.findAllCategories(lang, orderBy, fields);

        // Assert
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(categories.size()).isEqualTo(apiCategories.size());
        for (int i = 0; i < apiCategories.size(); ++i) {
            softly.assertThat(categories.get(i)).isEqualTo(apiCategories.get(i));
        }

        softly.assertAll();
    }
}
