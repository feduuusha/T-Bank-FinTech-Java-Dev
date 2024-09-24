package org.tbank.fintech.exception;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.tbank.fintech.controller.CategoriesRestController;
import org.tbank.fintech.controller.LocationsRestController;
import org.tbank.fintech.service.CategoryService;
import org.tbank.fintech.service.LocationService;

import java.util.NoSuchElementException;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the {@link GlobalExceptionHandlerControllerAdvice}
 */
@WebMvcTest(controllers = {CategoriesRestController.class, LocationsRestController.class})
public class GlobalExceptionHandlerControllerAdviceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LocationService locationService;
    @MockBean
    private CategoryService categoryService;
    @Test
    void handleBindExceptionInCategoriesControllerTest() throws Exception {
        // given
        String incorrectPayload = """
                {
                	"slug": "show",
                	"nmae": "Legendary show"
                }""";

        // when
        mockMvc.perform(post("/api/v1/places/categories")
                .content(incorrectPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void handleBindExceptionInLocationsControllerTest() throws Exception {
        // given
        String incorrectPayload = """
                {
                         "plug": "orn",
                         "name": "ОРЕНБУРГ",
                         "timezone": "GMT+03:00",
                         "coords": {
                             "lat": 56.32688699999997,
                             "lon": 44.00598599999999
                         },
                         "language": "ru"
                     }""";

        // when
        mockMvc.perform(post("/api/v1/locations")
                        .content(incorrectPayload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void handleNoSuchElementExceptionInCategoriesController() throws Exception {
        // given
        Long categoryId = 0L;
        when(categoryService.findCategoryById(categoryId)).thenThrow(NoSuchElementException.class);

        // when
        mockMvc.perform(get("/api/v1/places/categories/{categoryId}", categoryId))
                .andExpect(status().isNotFound());
    }

    @Test
    void handleNoSuchElementExceptionInLocationsController() throws Exception {
        // given
        Long locationId = 0L;
        when(locationService.findLocationById(locationId)).thenThrow(NoSuchElementException.class);

        // when
        mockMvc.perform(get("/api/v1/locations/{locationId}", locationId))
                .andExpect(status().isNotFound());
    }
}
