package org.tbank.fintech.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Category {
    private Long id;
    private String slug;
    private String name;

    public Category(String slug, String name) {
        this.slug = slug;
        this.name = name;
    }
}
