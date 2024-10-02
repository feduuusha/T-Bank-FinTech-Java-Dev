package org.tbank.fintech.repository.impl;

import org.springframework.stereotype.Repository;
import org.tbank.fintech.entity.Category;
import org.tbank.fintech.repository.CategoryRepository;

import java.util.List;

@Repository
public class ConcurrentHashMapCategoryRepository extends ConcurrentHashMapRepository<Category> implements CategoryRepository {
    @Override
    public void initializeByListOfCategories(List<Category> categories) {
        for (Category category : categories) {
            this.map.put(category.getId(), category);
            lastIndex = Math.max(lastIndex, category.getId() + 1);
        }
    }

    @Override
    public Category save(Category elem) {
        elem.setId(lastIndex);
        this.map.put(lastIndex++, elem);
        return elem;
    }
}
