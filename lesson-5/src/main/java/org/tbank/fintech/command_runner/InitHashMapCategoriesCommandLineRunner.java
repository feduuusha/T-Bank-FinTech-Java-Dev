package org.tbank.fintech.command_runner;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.tbank.fintech.clients.impl.RestClientCategoriesRestClient;
import org.tbank.fintech.entity.Category;
import org.tbank.fintech.executor_timer_starter.execution_timer.ExecutionTimer;
import org.tbank.fintech.repository.impl.ConcurrentHashMapCategoryRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class InitHashMapCategoriesCommandLineRunner implements CommandLineRunner {

    private final RestClientCategoriesRestClient categoriesRestClient;
    private final ConcurrentHashMapCategoryRepository categoryRepository;

    @ExecutionTimer
    @Override
    public void run(String... args) {
        List<Category> categories = this.categoriesRestClient.findAllCategories("ru", "slug", List.of("id", "slug", "name"));
        this.categoryRepository.initializeByListOfCategories(categories);
    }
}
