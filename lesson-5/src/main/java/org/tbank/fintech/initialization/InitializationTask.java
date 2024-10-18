package org.tbank.fintech.initialization;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.tbank.fintech.clients.CategoriesRestClient;
import org.tbank.fintech.clients.LocationsRestClient;
import org.tbank.fintech.entity.Category;
import org.tbank.fintech.entity.Location;
import org.tbank.fintech.repository.CategoryRepository;
import org.tbank.fintech.repository.LocationRepository;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class InitializationTask implements Runnable {

    private final ExecutorService initExecutorService;
    private final CategoriesRestClient categoriesRestClient;
    private final LocationsRestClient locationsRestClient;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;

    @Override
    public void run() {
        log.debug("Calling the InitializationTask.run() method");
        Callable<Void> categoriesInitTask = () -> {
            List<Category> categories = categoriesRestClient.findAllCategories("ru", "slug", List.of("id", "slug", "name"));
            categoryRepository.initializeByListOfCategories(categories);
            return null;
        };
        Callable<Void> locationsInitTask = () -> {
            List<Location> locations = locationsRestClient.findAllLocations("ru", "slug", List.of("slug", "name", "timezone", "coords", "language"));
            HashSet<String> slugs = new HashSet<>();
            for (Location location : locationRepository.findAll()) {
                slugs.add(location.getSlug());
            }
            for (Location location : locations) {
                if (!slugs.contains(location.getSlug())) {
                    locationRepository.save(location);
                }
            }
            return null;
        };
        try {
            long start = System.currentTimeMillis();
            initExecutorService.invokeAll(List.of(locationsInitTask, categoriesInitTask), 1, TimeUnit.MINUTES);
            log.info("Initialization process was completed in " + (System.currentTimeMillis() - start) + " ms");
        } catch (Exception e) {
            log.error("Error while initialization: " + e.getMessage());
            throw new IllegalStateException(e);
        }
    }
}
