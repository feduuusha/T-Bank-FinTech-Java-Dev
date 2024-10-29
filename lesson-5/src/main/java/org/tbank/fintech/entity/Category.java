package org.tbank.fintech.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tbank.fintech.entity.memento.CategoryMemento;
import org.tbank.fintech.entity.memento.Originator;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Category implements Originator<CategoryMemento> {
    private Long id;
    private String slug;
    private String name;

    public Category(String slug, String name) {
        this.slug = slug;
        this.name = name;
    }

    @Override
    public CategoryMemento createMemento() {
        return new CategoryMemento(slug, name);
    }


    @Override
    public void restore(CategoryMemento memento) {
        this.slug = memento.slug();
        this.name = memento.name();
    }
}
