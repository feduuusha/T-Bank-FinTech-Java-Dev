package org.tbank.fintech.clients;

import org.tbank.fintech.entity.Category;

import java.util.List;

public interface CategoriesRestClient {
    List<Category> findAllCategories(String lang, String orderBy, List<String> fields);
}
