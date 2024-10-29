package org.tbank.fintech.repository.impl;

import org.springframework.stereotype.Repository;
import org.tbank.fintech.entity.Category;
import org.tbank.fintech.repository.CategoryRepository;

@Repository
public class ConcurrentHashMapCategoryRepository extends ConcurrentHashMapRepository<Category> implements CategoryRepository {
    @Override
    public Category save(Category elem) {
        elem.setId(lastIndex);
        this.map.put(lastIndex++, elem);
        return elem;
    }
}
