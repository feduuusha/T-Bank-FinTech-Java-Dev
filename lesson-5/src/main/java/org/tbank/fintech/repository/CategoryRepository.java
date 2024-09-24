package org.tbank.fintech.repository;

import org.tbank.fintech.entity.Category;

import java.util.List;

public interface CategoryRepository extends CrudRepository<Category> {
    void initializeByListOfCategories(List<Category> categories);
}
