package org.tbank.fintech.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import org.tbank.fintech.controller.payload.category.NewCategoryPayload;
import org.tbank.fintech.controller.payload.category.UpdateCategoryPayload;
import org.tbank.fintech.entity.Category;
import org.tbank.fintech.executor_timer_starter.execution_timer.ExecutionTimer;
import org.tbank.fintech.service.CategoryService;


import java.util.List;

@ExecutionTimer
@RestController
@RequestMapping("/api/v1/places/categories")
@RequiredArgsConstructor
public class CategoriesRestController {

    private final CategoryService categoryService;

    @GetMapping
    public List<Category> findAllCategories() {
        return this.categoryService.findAllCategories();
    }

    @GetMapping("/{categoryId:\\d+}")
    public Category findCategoryById(@PathVariable Long categoryId) {
        return this.categoryService.findCategoryById(categoryId);
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(@Valid @RequestBody NewCategoryPayload payload,
                                                   BindingResult bindingResult,
                                                   UriComponentsBuilder uriComponentsBuilder) throws BindException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        } else {
            Category category = this.categoryService.createCategory(payload.slug(), payload.name());
            return ResponseEntity
                    .created(uriComponentsBuilder
                            .replacePath("/api/v1/places/categories/{categoryId}")
                            .build(category.getId()))
                    .body(category);
        }
    }

    @PutMapping("/{categoryId:\\d+}")
    public ResponseEntity<Void> updateCategoryById(@Valid @RequestBody UpdateCategoryPayload payload,
                                                BindingResult bindingResult,
                                                @PathVariable Long categoryId) throws BindException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        } else {
            this.categoryService.updateCategoryById(categoryId, payload.slug(), payload.name());
            return ResponseEntity.noContent().build();
        }
    }

    @DeleteMapping("/{categoryId:\\d+}")
    public ResponseEntity<Void> deleteCategoryById(@PathVariable Long categoryId) {
        this.categoryService.deleteCategoryById(categoryId);
        return ResponseEntity.noContent().build();
    }

}
