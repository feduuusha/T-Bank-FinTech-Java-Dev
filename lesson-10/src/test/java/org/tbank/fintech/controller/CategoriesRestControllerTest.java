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
    @DisplayName("Controller create category test, method:post endpoint:/api/v1/places/categories")
    public void createCategoryTest() throws Exception {
        // given
        String payload = """
                {
                	"slug": "show",
                	"name": "Legendary show"
                }""";
        Category testCategory = new Category(0L, "show", "Legendary show");
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
    @DisplayName("Controller delete category by id test, method:delete endpoint:/api/v1/places/categories/{categoryId}")
    public void deleteCategoryByIdTest() throws Exception {
        // given
        Long categoryId = 0L;

        // when
        mockMvc.perform(delete("/api/v1/places/categories/{categoryId}", categoryId))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Controller find all categories test, method:get endpoint:/api/v1/places/categories")
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
    @DisplayName("Controller find category by id test, method:get endpoint:/api/v1/places/categories/{categoryId}")
    public void findCategoryByIdTest() throws Exception {
        // given
        Long categoryId = 0L;
        Category testCategory = new Category(categoryId,"show", "Legendary show");
        when(categoryService.findCategoryById(categoryId)).thenReturn(testCategory);

        // then
        mockMvc.perform(get("/api/v1/places/categories/{categoryId}", categoryId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("{\"id\":0,\"slug\":\"show\",\"name\":\"Legendary show\"}"));
    }

    @Test
    @DisplayName("Controller update category by id, method:put endpoint:/api/v1/places/categories/{categoryId}")
    public void updateCategoryByIdTest() throws Exception {
        // given
        String payload = """
                {
                	"slug": "cow",
                	"name": "cat"
                }""";
        Long categoryId = 0L;
        Category testCategory = new Category(categoryId,"msk", "Moskow");
        when(categoryService.findCategoryById(categoryId)).thenReturn(testCategory);

        // then
        mockMvc.perform(put("/api/v1/places/categories/{categoryId}", categoryId).content(payload).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}