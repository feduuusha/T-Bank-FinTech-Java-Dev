package org.tbank.fintech.service;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tbank.fintech.entity.Category;
import org.tbank.fintech.repository.CategoryRepository;
import org.tbank.fintech.service.impl.CategoryServiceImpl;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test class for the {@link CategoryServiceImpl}
 */
@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;
    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    public void findAllCategories_repositoryContainsThreeCategory_shouldReturnListOfThreeCategories() {
        // Arrange
        List<Category> repoCategories = List.of(
                new Category("slug1", "name1"),
                new Category("slug2", "name2"),
                new Category("slug3", "name3")
        );
        when(categoryRepository.findAll()).thenReturn(repoCategories);

        // Act
        List<Category> categories = categoryService.findAllCategories();

        // Assert
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(categories.size()).isEqualTo(repoCategories.size());
        for (int i = 0; i < repoCategories.size(); ++i) {
            softly.assertThat(repoCategories.get(i)).isEqualTo(categories.get(i));
        }

        softly.assertAll();
    }

    @Test
    public void findCategoryById_repositoryContainsCategory_shouldReturnCategory() {
        // Arrange
        Category repoCategory = new Category("slug", "name");
        Long categoryId = 1L;
        repoCategory.setId(categoryId);
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(repoCategory));

        // Act
        Category category = categoryService.findCategoryById(categoryId);

        // Assert
        assertThat(category).isEqualTo(repoCategory);
    }

    @Test
    public void findCategoryById_repositoryNotContainsCategory_shouldThrowNoSuchElementException() {
        // Arrange
        Long categoryId = 1L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act
        // Assert
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(
                () -> categoryService.findCategoryById(categoryId)
        ).withMessage("Category with id=" + categoryId + " was not found");
    }

    @Test
    public void createCategory_shouldCreateCategory() {
        // Arrange
        String slug = "slug";
        String name = "name";

        // Act
        categoryService.createCategory(slug, name);

        // Assert
        ArgumentCaptor<Category> categoryArgumentCaptor = ArgumentCaptor.forClass(Category.class);
        verify(categoryRepository).save(categoryArgumentCaptor.capture());
        Category category = categoryArgumentCaptor.getValue();
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(category.getName()).isEqualTo(name);
        softly.assertThat(category.getSlug()).isEqualTo(slug);

        softly.assertAll();
    }

    @Test
    public void updateCategory_shouldUpdateCategory() {
        // Arrange
        Category repoCategory = new Category("slug", "name");
        Long categoryId = 1L;
        repoCategory.setId(categoryId);
        String expectedName = "new_name";
        String expectedSlug = "new_slug";
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(repoCategory));

        // Act
        categoryService.updateCategoryById(categoryId, expectedSlug, expectedName);

        // Assert
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(repoCategory.getId()).isEqualTo(categoryId);
        softly.assertThat(repoCategory.getName()).isEqualTo(expectedName);
        softly.assertThat(repoCategory.getSlug()).isEqualTo(expectedSlug);

        softly.assertAll();
    }

    @Test
    public void deleteCategoryById_shouldDeleteCategory() {
        // Arrange
        Long categoryId = 5L;

        // Act
        categoryService.deleteCategoryById(categoryId);

        // Assert
        verify(categoryRepository).deleteById(categoryId);
    }
}
