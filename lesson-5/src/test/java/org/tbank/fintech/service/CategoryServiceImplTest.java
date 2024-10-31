package org.tbank.fintech.service;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tbank.fintech.entity.Category;
import org.tbank.fintech.entity.memento.CategoryMemento;
import org.tbank.fintech.repository.CategoryRepository;
import org.tbank.fintech.service.impl.CategoryServiceImpl;
import org.tbank.fintech.util.Caretaker;

import java.util.ArrayList;
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
    @Mock
    private Caretaker<Long, CategoryMemento> categoriesCaretaker;
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
        when(categoryRepository.save(new Category(slug, name))).thenReturn(new Category(1L, slug, name));

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
        when(categoriesCaretaker.get(categoryId)).thenReturn(new ArrayList<>());
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

    @Test
    @DisplayName("findAllVersionsOfCategoryById should return list of category mementos because category with provided id is exist")
    public void findAllVersionsOfCategoryByIdTest() {
        // Arrange
        Long categoryId = 5L;
        List<CategoryMemento> mementos = List.of(
                new CategoryMemento("slug1", "name1"),
                new CategoryMemento("slug2", "name2"),
                new CategoryMemento("slug3", "name3")
        );
        when(categoriesCaretaker.get(categoryId)).thenReturn(mementos);

        // Act
        var list = categoryService.findAllVersionsOfCategoryById(categoryId);

        // Assert
        assertThat(list).isEqualTo(mementos);
    }

    @Test
    @DisplayName("findAllVersionsOfCategoryById should throw NoSuchElementException because category with provided id is not exist")
    public void findAllVersionsOfCategoryByIdUnSuccessfulTest() {
        // Arrange
        Long categoryId = 5L;
        when(categoriesCaretaker.get(categoryId)).thenReturn(null);

        // Act
        // Assert
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> categoryService.findAllVersionsOfCategoryById(categoryId))
                .withMessage("Category with ID: 5 has never existed");
    }

    @Test
    @DisplayName("findVersionOfCategoryByIndex should return category memento because category with provided id is exist and index is correct")
    public void findVersionOfCategoryByIndexTest() {
        // Arrange
        Long categoryId = 5L;
        int versionIndex = 1;
        List<CategoryMemento> mementos = List.of(
                new CategoryMemento("slug1", "name1"),
                new CategoryMemento("slug2", "name2"),
                new CategoryMemento("slug3", "name3")
        );
        when(categoriesCaretaker.get(categoryId)).thenReturn(mementos);

        // Act
        var memento = categoryService.findVersionOfCategoryByIndex(categoryId, versionIndex);

        // Assert
        assertThat(memento).isEqualTo(mementos.get(versionIndex));
    }

    @Test
    @DisplayName("findVersionOfCategoryByIndex should throw NoSuchElementException because index is incorrect")
    public void findVersionOfCategoryByIndexUnSuccessfulTest() {
        // Arrange
        Long categoryId = 5L;
        Integer versionIndex = 5;
        List<CategoryMemento> mementos = List.of(
                new CategoryMemento("slug1", "name1"),
                new CategoryMemento("slug2", "name2"),
                new CategoryMemento("slug3", "name3")
        );
        when(categoriesCaretaker.get(categoryId)).thenReturn(mementos);

        // Act
        // Assert
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> categoryService.findVersionOfCategoryByIndex(categoryId, versionIndex))
                .withMessage("Category version with index " + versionIndex + " not found");
    }

    @Test
    @DisplayName("restoreVersionOfCategory should restore category by index of memento")
    public void restoreVersionOfCategoryTest() {
        // Arrange
        Long categoryId = 5L;
        int versionIndex = 2;
        List<CategoryMemento> mementos = new ArrayList<>();
        mementos.add(new CategoryMemento("slug1", "name1"));
        mementos.add(new CategoryMemento("slug2", "name2"));
        mementos.add(new CategoryMemento("slug3", "name3"));

        when(categoriesCaretaker.get(categoryId)).thenReturn(mementos);
        Category category = new Category(categoryId, "slug", "name");
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        // Act
        Category categoryResult = categoryService.restoreVersionOfCategory(categoryId, versionIndex);

        // Assert
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(categoryResult.getSlug()).isEqualTo(mementos.get(versionIndex).slug());
        softly.assertThat(categoryResult.getName()).isEqualTo(mementos.get(versionIndex).name());

        softly.assertAll();
    }
}
