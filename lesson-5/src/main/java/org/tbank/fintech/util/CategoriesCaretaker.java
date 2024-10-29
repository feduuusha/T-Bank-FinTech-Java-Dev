package org.tbank.fintech.util;

import org.springframework.stereotype.Component;
import org.tbank.fintech.entity.memento.CategoryMemento;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CategoriesCaretaker implements Caretaker<Long, CategoryMemento> {

    private final ConcurrentHashMap<Long, List<CategoryMemento>> map;

    public CategoriesCaretaker() {
        this.map = new ConcurrentHashMap<>();
    }

    @Override
    public List<CategoryMemento> get(Long id) {
        return map.get(id);
    }

    @Override
    public List<CategoryMemento> put(Long id, List<CategoryMemento> value) {
        return map.put(id, value);
    }
}
