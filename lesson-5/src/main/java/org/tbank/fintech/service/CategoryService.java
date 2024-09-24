package org.tbank.fintech.service;

import org.tbank.fintech.entity.Category;

import java.util.List;

public interface CategoryService {
    List<Category> findAllCategories();

    Category findCategoryById(Long categoryId);

    Category createCategory(String slug, String name);

    void updateCategoryById(Long categoryId, String slug, String name);

    void deleteCategoryById(Long categoryId);
}
