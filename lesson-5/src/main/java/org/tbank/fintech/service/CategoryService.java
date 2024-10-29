package org.tbank.fintech.service;

import org.tbank.fintech.entity.Category;
import org.tbank.fintech.entity.memento.CategoryMemento;

import java.util.List;

public interface CategoryService {
    List<Category> findAllCategories();

    Category findCategoryById(Long categoryId);

    Category createCategory(String slug, String name);

    void updateCategoryById(Long categoryId, String slug, String name);

    void deleteCategoryById(Long categoryId);

    List<CategoryMemento> findAllVersionsOfCategoryById(Long categoryId);

    CategoryMemento findVersionOfCategoryByIndex(Long categoryId, Integer versionIndex);

    Category restoreVersionOfCategory(Long categoryId, Integer versionIndex);
}
