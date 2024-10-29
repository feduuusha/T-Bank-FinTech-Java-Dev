package org.tbank.fintech.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import org.tbank.fintech.controller.payload.category.NewCategoryPayload;
import org.tbank.fintech.controller.payload.category.UpdateCategoryPayload;
import org.tbank.fintech.entity.Category;
import org.tbank.fintech.entity.memento.CategoryMemento;
import org.tbank.fintech.executor_timer_starter.execution_timer.ExecutionTimer;
import org.tbank.fintech.service.CategoryService;


import java.util.List;

@Tag(name = "Categories REST Controller", description = "Controller for CRUD operations with categories and for restore versions of category")
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

    @Operation(description = "Get all versions of specific category by its id",
                responses = {
                        @ApiResponse(responseCode = "200", description = "Return all versions of category when category with specified id is exist", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = CategoryMemento.class)))),
                        @ApiResponse(responseCode = "404", description = "Return when category with specified id is not exist", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class))),
                        @ApiResponse(responseCode = "400", description = "Return when request param 'categoryId' is not specified or it is null", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class))),
    })
    @GetMapping("/{categoryId:\\d+}/versions")
    public List<CategoryMemento> findAllVersionsOfCategory(@Valid @NotNull @PathVariable Long categoryId) {
        return this.categoryService.findAllVersionsOfCategoryById(categoryId);
    }

    @Operation(description = "Get specific versions of specific category by its id and by version index",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Return specific version of category when category with specified id is exist and when category version with specified index is exist", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CategoryMemento.class))),
                    @ApiResponse(responseCode = "404", description = "Return when category with specified id is not exist or when versionIndex is incorrect", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "400", description = "Return when request params 'categoryId' or 'versionIndex' are not specified or it is null", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class))),
            })
    @GetMapping("/{categoryId:\\d+}/versions/{versionIndex:\\d+}")
    public CategoryMemento findVersionOfCategoryByIndex(@PathVariable Long categoryId,
                                                        @PathVariable Integer versionIndex) {
        return this.categoryService.findVersionOfCategoryByIndex(categoryId, versionIndex);
    }

    @Operation(description = "Restores the specific state of category to the current category with specified id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Return restored category", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Category.class))),
                    @ApiResponse(responseCode = "404", description = "Return when category with specified id is not exist or when versionIndex is incorrect", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "400", description = "Return when request params 'categoryId' or 'versionIndex' are not specified or it is null", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class))),
            })
    @PostMapping("/{categoryId:\\d+}/restore/{versionIndex:\\d+}")
    public Category restoreVersionOfCategory(@PathVariable Long categoryId,
                                             @PathVariable Integer versionIndex) {
        return this.categoryService.restoreVersionOfCategory(categoryId, versionIndex);
    }
}
