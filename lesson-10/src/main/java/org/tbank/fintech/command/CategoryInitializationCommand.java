package org.tbank.fintech.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.tbank.fintech.clients.CategoriesRestClient;
import org.tbank.fintech.entity.Category;
import org.tbank.fintech.repository.CategoryRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CategoryInitializationCommand implements Command<Void> {

    private final CategoriesRestClient categoriesRestClient;
    private final CategoryRepository categoryRepository;

    @Override
    public Void call() {
        List<Category> categories = categoriesRestClient.findAllCategories("ru", "slug", List.of("id", "slug", "name"));
        categoryRepository.initializeByListOfCategories(categories);
        return null;
    }

    @Override
    public String getType() {
        return "initCategories";
    }
}
