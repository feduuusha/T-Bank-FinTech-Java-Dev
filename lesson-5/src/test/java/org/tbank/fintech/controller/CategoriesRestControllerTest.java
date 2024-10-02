package org.tbank.fintech.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.tbank.fintech.entity.Category;
import org.tbank.fintech.service.CategoryService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the {@link CategoriesRestController}
 */
@WebMvcTest(controllers = {CategoriesRestController.class})
public class CategoriesRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Test
    @DisplayName("Test create category")
    public void createCategoryTest() throws Exception {
        // given
        String payload = """
                {
                	"slug": "show",
                	"name": "Legendary show"
                }""";
        Category testCategory = new Category("show", "Legendary show");
        testCategory.setId(0L);
        when(categoryService.createCategory("show", "Legendary show")).thenReturn(testCategory);

        // when
        mockMvc.perform(post("/api/v1/places/categories")
                        .content(payload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/v1/places/categories/0"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("{\"id\":0,\"slug\":\"show\",\"name\":\"Legendary show\"}"));
    }

    @Test
    @DisplayName("Test delete category by id")
    public void deleteCategoryByIdTest() throws Exception {
        // given
        String categoryId = "0";

        // when
        mockMvc.perform(delete("/api/v1/places/categories/{categoryId}", categoryId))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Test find all categories")
    public void findAllCategoriesTest() throws Exception {
        // given
        List<Category> categories = List.of(
                new Category("oren", "Orenburg"),
                new Category("kzn", "Kazan")
        );
        when(categoryService.findAllCategories()).thenReturn(categories);

        //when
        mockMvc.perform(get("/api/v1/places/categories"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("[{\"id\":null,\"slug\":\"oren\",\"name\":\"Orenburg\"},{\"id\":null,\"slug\":\"kzn\",\"name\":\"Kazan\"}]"));
    }

    @Test
    @DisplayName("Test find category by id")
    public void findCategoryByIdTest() throws Exception {
        // given
        String categoryId = "0";
        Category testCategory = new Category("show", "Legendary show");
        testCategory.setId(0L);
        when(categoryService.findCategoryById(0L)).thenReturn(testCategory);

        // then
        mockMvc.perform(get("/api/v1/places/categories/{categoryId}", categoryId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("{\"id\":0,\"slug\":\"show\",\"name\":\"Legendary show\"}"));
    }

    @Test
    @DisplayName("Test update category by id")
    public void updateCategoryByIdTest() throws Exception {
        // given
        String payload = """
                {
                	"slug": "cow",
                	"name": "cat"
                }""";
        Long categoryId = 0L;
        Category testCategory = new Category("msk", "Moskow");
        testCategory.setId(0L);
        when(categoryService.findCategoryById(categoryId)).thenReturn(testCategory);

        // then
        mockMvc.perform(put("/api/v1/places/categories/{categoryId}", categoryId).content(payload).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
