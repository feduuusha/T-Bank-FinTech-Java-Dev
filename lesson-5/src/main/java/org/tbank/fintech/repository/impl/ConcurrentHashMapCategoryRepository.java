package org.tbank.fintech.repository.impl;

import org.springframework.stereotype.Repository;
import org.tbank.fintech.entity.Category;
import org.tbank.fintech.repository.CategoryRepository;

import java.util.HashSet;
import java.util.List;

@Repository
public class ConcurrentHashMapCategoryRepository extends ConcurrentHashMapRepository<Category> implements CategoryRepository {
    @Override
    public void initializeByListOfCategories(List<Category> categories) {
        HashSet<String> slugs = new HashSet<>();
        for (Category category : this.map.values()) {
            slugs.add(category.getSlug());
        }
        for (Category category : categories) {
            if (!this.map.containsKey(category.getId())) {
                this.map.put(category.getId(), category);
                lastIndex = Math.max(lastIndex, category.getId() + 1);
            } else {
                if (!slugs.contains(category.getSlug())) {
                    save(category);
                }
            }
        }
    }

    @Override
    public Category save(Category elem) {
        elem.setId(lastIndex);
        this.map.put(lastIndex++, elem);
        return elem;
    }
}
