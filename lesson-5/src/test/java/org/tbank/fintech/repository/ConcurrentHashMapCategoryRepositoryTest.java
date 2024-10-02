package org.tbank.fintech.repository;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tbank.fintech.entity.Category;
import org.tbank.fintech.repository.impl.ConcurrentHashMapCategoryRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for the {@link org.tbank.fintech.repository.impl.ConcurrentHashMapCategoryRepository}
 */
class ConcurrentHashMapCategoryRepositoryTest {

    private final CategoryRepository hashMapCategoryRepository = new ConcurrentHashMapCategoryRepository();

    @Test
    @DisplayName("findAllCategories should return all saved categories")
    void testFindAllCategories() {
        // given
        Category category1 = new Category( "Category 1", "Description 1");
        Category category2 = new Category("Category 2", "Description 2");
        hashMapCategoryRepository.save(category1);
        hashMapCategoryRepository.save(category2);

        // when
        List<Category> allCategories = hashMapCategoryRepository.findAll();

        // then
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(allCategories.size()).as(() -> "Expected 2 categories, but got " + allCategories.size()).isEqualTo(2);
        softly.assertThat(allCategories.contains(category1)).as("Expected category1 to be present in the list").isTrue();
        softly.assertThat(allCategories.contains(category2)).as("Expected category2 to be present in the list").isTrue();

        softly.assertAll();
    }

    @Test
    @DisplayName("saveCategory should save a new category and assign ID")
    void testSaveCategory() {
        // given
        Category category = new Category("New Category", "New Description");

        // when
        Category savedCategory = hashMapCategoryRepository.save(category);

        // then
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(savedCategory.getId()).as(() -> "Expected ID to be 1, but got " + savedCategory.getId()).isEqualTo(1);
        softly.assertThat(savedCategory).as("Expected savedCategory to be equal to category").isEqualTo(category);
        softly.assertThat(hashMapCategoryRepository.findAll().contains(savedCategory)).as("Expected savedCategory to be present in the list").isTrue();

        softly.assertAll();
    }

    @Test
    @DisplayName("findCategoryById should find a category by its ID")
    void testFindCategoryById() {
        // given
        Category category = new Category("Existing Category", "Existing Description");
        hashMapCategoryRepository.save(category);

        // when
        Optional<Category> foundCategory = hashMapCategoryRepository.findById(1L);

        // then
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(foundCategory.isPresent()).as("Expected category to be present").isTrue();
        softly.assertThat(foundCategory.get()).as("Expected foundCategory to be equal to category").isEqualTo(category);

        softly.assertAll();
    }

    @Test
    @DisplayName("findCategoryById should return empty Optional if category not found")
    void testFindCategoryByIdNotFound() {
        // given
        Optional<Category> foundCategory = hashMapCategoryRepository.findById(100L);

        // then
        assertThat(foundCategory.isPresent()).as("Expected category to be not present").isFalse();
    }

    @Test
    @DisplayName("deleteCategoryById should delete a category by its ID")
    void testDeleteCategoryById() {
        // given
        Category category = new Category( "Category to Delete", "Description to Delete");
        hashMapCategoryRepository.save(category);

        // when
        hashMapCategoryRepository.deleteById(1L);

        // then
        assertThat(hashMapCategoryRepository.findById(1L).isPresent()).as("Expected category to be deleted").isFalse();
    }

    @Test
    @DisplayName("initializeByListOfCategories should initialize the repository with a list of categories")
    void testInitializeByListOfCategories() {
        // given
        Category category1 = new Category(1L, "Category 1", "Description 1");
        Category category2 = new Category(2L, "Category 2", "Description 2");
        List<Category> categories = List.of(category1, category2);

        // when
        hashMapCategoryRepository.initializeByListOfCategories(categories);

        // then
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(hashMapCategoryRepository.findById(1L).isPresent()).isTrue();
        softly.assertThat(hashMapCategoryRepository.findById(2L).isPresent()).isTrue();
        softly.assertThat(hashMapCategoryRepository.findAll().size()).as(() -> "Expected 2 categories, but got " + hashMapCategoryRepository.findAll().size()).isEqualTo(2);

        softly.assertAll();
    }
}