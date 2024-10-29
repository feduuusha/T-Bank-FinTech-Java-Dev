package org.tbank.fintech.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.tbank.fintech.entity.Category;
import org.tbank.fintech.entity.memento.CategoryMemento;
import org.tbank.fintech.repository.CategoryRepository;
import org.tbank.fintech.service.CategoryService;
import org.tbank.fintech.util.Caretaker;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final Caretaker<Long, CategoryMemento> categoriesCaretaker;

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
        var category = this.categoryRepository.save(new Category(slug, name));
        var categoryCaretaker = new ArrayList<CategoryMemento>();
        categoryCaretaker.add(category.createMemento());
        categoriesCaretaker.put(category.getId(), categoryCaretaker);
        return category;
    }

    @Override
    public void updateCategoryById(Long categoryId, String slug, String name) {
        Category category = this.findCategoryById(categoryId);
        category.setSlug(slug);
        category.setName(name);
        this.categoryRepository.updateById(categoryId, category);
        categoriesCaretaker.get(categoryId).add(category.createMemento());
    }

    @Override
    public void deleteCategoryById(Long categoryId) {
        this.categoryRepository.deleteById(categoryId);
    }

    @Override
    public List<CategoryMemento> findAllVersionsOfCategoryById(Long categoryId) {
        return Optional.ofNullable(categoriesCaretaker.get(categoryId)).orElseThrow(() -> new NoSuchElementException("Category with ID: " + categoryId + " has never existed"));
    }

    @Override
    public CategoryMemento findVersionOfCategoryByIndex(Long categoryId, Integer versionIndex) {
        var caretaker = findAllVersionsOfCategoryById(categoryId);
        try {
            return caretaker.get(versionIndex);
        } catch (IndexOutOfBoundsException exception) {
            throw new NoSuchElementException("Category version with index " + versionIndex + " not found");
        }
    }

    @Override
    public Category restoreVersionOfCategory(Long categoryId, Integer versionIndex) {
        var category = findCategoryById(categoryId);
        var memento = findVersionOfCategoryByIndex(categoryId, versionIndex);
        category.restore(memento);
        categoriesCaretaker.get(categoryId).add(category.createMemento());
        return category;
    }
}
