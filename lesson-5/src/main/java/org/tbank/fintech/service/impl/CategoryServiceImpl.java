package org.tbank.fintech.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.tbank.fintech.entity.Category;
import org.tbank.fintech.repository.CategoryRepository;
import org.tbank.fintech.service.CategoryService;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<Category> findAllCategories() {
        return this.categoryRepository.findAll();
    }

    @Override
    public Category findCategoryById(Long categoryId) {
        return this.categoryRepository.findById(categoryId).orElseThrow(() -> new NoSuchElementException("Category with id=" + categoryId + " was not found"));
    }

    @Override
    public Category createCategory(String slug, String name) {
        return this.categoryRepository.save(new Category(slug, name));
    }

    @Override
    public void updateCategoryById(Long categoryId, String slug, String name) {
        Category category = this.findCategoryById(categoryId);
        category.setSlug(slug);
        category.setName(name);
        this.categoryRepository.updateById(categoryId, category);
    }

    @Override
    public void deleteCategoryById(Long categoryId) {
        this.categoryRepository.deleteById(categoryId);
    }
}
