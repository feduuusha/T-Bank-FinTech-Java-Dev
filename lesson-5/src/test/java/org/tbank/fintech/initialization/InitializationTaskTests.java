package org.tbank.fintech.initialization;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.tbank.fintech.clients.CategoriesRestClient;
import org.tbank.fintech.clients.LocationsRestClient;
import org.tbank.fintech.config.ExecutorServiceBeans;
import org.tbank.fintech.entity.Category;
import org.tbank.fintech.entity.Coords;
import org.tbank.fintech.entity.Location;
import org.tbank.fintech.repository.CategoryRepository;
import org.tbank.fintech.repository.LocationRepository;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {InitializationTask.class, ExecutorServiceBeans.class})
public class InitializationTaskTests {

    @Autowired
    private InitializationTask initializationTask;

    @SpyBean(name = "initExecutorService")
    private ExecutorService initExecutorService;
    @MockBean
    private CategoriesRestClient categoriesRestClient;
    @MockBean
    private LocationsRestClient locationsRestClient;
    @MockBean
    private CategoryRepository categoryRepository;
    @MockBean
    private LocationRepository locationRepository;

    @Test
    @DisplayName("Initialization task should call the client's methods and write data to the repository")
    public void runTest() throws Exception {
        // Arrange
        List<Location> locations = List.of(
                new Location("slug", "name", "timezone", new Coords(50D, 40D), "ru"),
                new Location("slug1", "name1", "timezone1", new Coords(60D, 30D), "ru1"),
                new Location("slug2", "name2", "timezone2", new Coords(70D, 20D), "ru2"),
                new Location("slug3", "name3", "timezone3", new Coords(80D, 10D), "ru3")
        );
        List<Category> categories = List.of(
                new Category("slug", "name"),
                new Category("slug1", "name1"),
                new Category("slug3", "name2")

        );
        when(locationsRestClient.findAllLocations("ru", "slug", List.of("slug", "name", "timezone", "coords", "language")))
                .thenReturn(locations);
        when(categoriesRestClient.findAllCategories("ru", "slug", List.of("id", "slug", "name")))
                .thenReturn(categories);
        when(locationRepository.findAll()).thenReturn(List.of(new Location("slug", "name", "timezone", new Coords(), "ru")));

        // Act
        initializationTask.run();

        // Assert
        verify(categoryRepository).initializeByListOfCategories(categories);
        for (var location : locations) {
            if (!location.getSlug().equals("slug"))
                verify(locationRepository).save(location);
        }
        verify(initExecutorService).invokeAll(anyCollection(), eq(1L), eq(TimeUnit.MINUTES));

    }
}
