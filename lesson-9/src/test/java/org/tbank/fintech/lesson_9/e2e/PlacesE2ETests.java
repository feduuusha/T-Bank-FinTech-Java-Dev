package org.tbank.fintech.lesson_9.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.SoftAssertions;
import org.hamcrest.text.MatchesPattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.tbank.fintech.lesson_9.entity.Place;
import org.tbank.fintech.lesson_9.exception.ExceptionMessage;
import org.tbank.fintech.lesson_9.repository.PlaceRepository;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
public class PlacesE2ETests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PlaceRepository placeRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry propertyRegistry) {
        propertyRegistry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        propertyRegistry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        propertyRegistry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @BeforeEach
    public void cleanDataBase() {
        placeRepository.deleteAll();
    }

    @Test
    @DisplayName("Method: GET, Endpoint: /api/v1/places should return all places from db")
    @WithMockUser(authorities = {"read_place"})
    public void findAllPlacesTest() throws Exception {
        // Arrange
        List<Place> placesInRepo = List.of(
                new Place(null, "place1", 50D, 60D, "name1", "timezone1", "ru1", null),
                new Place(null, "place2", 60D, 50D, "name2", "timezone2", "ru2", null),
                new Place(null, "place3", 70D, 40D, "name3", "timezone3", "ru3", null)
        );
        placesInRepo = placeRepository.saveAll(placesInRepo);

        // Act
        // Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/places"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(placesInRepo)));
    }

    @Test
    @DisplayName("Method: POST, Endpoint: /api/v1/places should save place in db")
    @WithMockUser(authorities = {"create_place"})
    public void createPlaceTest() throws Exception {
        // Arrange
        String placeName = "name";
        String placeSlug = "slug";
        Double lat = 60D;
        Double lon = 50D;
        String timezone = "timezone";
        String language = "ru";
        String requestBodyJson = "{\n" +
                "    \"name\": \"" + placeName + "\",\n" +
                "    \"slug\": \"" + placeSlug + "\",\n" +
                "    \"lat\":" + lat + ",\n" +
                "    \"lon\":" + lon + ",\n" +
                "    \"timezone\":\"" + timezone + "\",\n" +
                "    \"language\": \"" + language + "\"\n" +
                "}";

        // Act
        String response = mockMvc.perform(post("/api/v1/places").content(requestBodyJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", MatchesPattern.matchesPattern(".*/api/v1/places/\\d+$")))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        Place place = objectMapper.readValue(response, Place.class);
        Place expectedPlace = new Place(place.getId(), placeSlug, lat, lon, placeName, timezone, language, null);
        // Assert
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(place).isEqualTo(expectedPlace);
        softly.assertThat(placeRepository.findById(place.getId()).orElseThrow()).isEqualTo(expectedPlace);

        softly.assertAll();
    }

    @Test
    @DisplayName("Method: POST, Endpoint: /api/v1/places should throw 400, because request body is incorrect")
    @WithMockUser(authorities = {"create_place"})
    public void createPlaceUnSuccessfulTest() throws Exception {
        // Arrange
        String placeName = "name";
        String placeSlug = "slug";
        String timezone = "timezone";
        String language = "ru";
        double lat = 50D;
        String requestBodyJson = "{\n" +
                "    \"name\": \"" + placeName + "\",\n" +
                "    \"slug\": \"" + placeSlug + "\",\n" +
                "    \"lat\": " + lat + ",\n" +
                "    \"timezone\":\"" + timezone + "\",\n" +
                "    \"language\": \"" + language + "\"\n" +
                "}";
        String expectedErrorMessage = "org.springframework.validation.BeanPropertyBindingResult: 1 errors\n" +
                "Field error in object 'newPlacePayload' on field 'lon': rejected value [null]; codes [NotNull.newPlacePayload.lon,NotNull.lon,NotNull.java.lang.Double,NotNull]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [newPlacePayload.lon,lon]; arguments []; default message [lon]]; default message [must not be null]";

        // Act
        String response = mockMvc.perform(post("/api/v1/places").content(requestBodyJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        ExceptionMessage exceptionMessage = objectMapper.readValue(response, ExceptionMessage.class);

        // Assert
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(exceptionMessage.status()).isEqualTo(400);
        softly.assertThat(exceptionMessage.error()).isEqualTo(expectedErrorMessage);
        softly.assertThat(exceptionMessage.path()).isEqualTo("/api/v1/places");

        softly.assertAll();
    }

    @Test
    @DisplayName("Method: GET, Endpoint: /api/v1/places/{placeId} should throw 404 because place do not exist")
    @WithMockUser(authorities = {"read_place"})
    public void findPlaceByIdunSuccessfulTest() throws Exception {
        // Arrange
        Long placeId = 1000L;

        // Act
        String response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/places/{placeId}", placeId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        ExceptionMessage exceptionMessage = objectMapper.readValue(response, ExceptionMessage.class);

        // Assert
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(exceptionMessage.status()).isEqualTo(404);
        softly.assertThat(exceptionMessage.path()).isEqualTo("/api/v1/places/" + placeId);
        softly.assertThat(exceptionMessage.error()).isEqualTo("Place with id: " + placeId + " not found");

        softly.assertAll();
    }

    @Test
    @DisplayName("Method: GET, Endpoint: /api/v1/places/{placeId} should return place with specified id")
    @WithMockUser(authorities = {"read_place"})
    public void findPlaceByIdTest() throws Exception {
        // Arrange
        Place place = new Place(null, "place", 50D, 60D, "name1", "timezone1", "ru1", null);
        place = placeRepository.save(place);

        // Act
        // Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/places/{placeId}", place.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(place)));
    }


    @Test
    @DisplayName("Method: PUT, Endpoint: /api/v1/places/{placeId} should update place in db")
    @WithMockUser(authorities = {"update_place"})
    public void updatePlaceById() throws Exception {
        // Arrange
        Place place = new Place(null, "place", 50D, 60D, "name1", "timezone1", "ru1", null);
        place = placeRepository.save(place);
        String newPlaceName = "newPlaceName";
        String newPlaceSlug = "newSlug";
        Double newLat = 70D;
        Double newLon = 80D;
        String newTimezone = "newTimezone";
        String newLanguage = "ru2";
        String requestBodyJson = "{\n" +
                "    \"name\": \"" + newPlaceName + "\",\n" +
                "    \"slug\": \"" + newPlaceSlug + "\",\n" +
                "    \"lat\":" + newLat + ",\n" +
                "    \"lon\":" + newLon + ",\n" +
                "    \"timezone\": \"" + newTimezone + "\",\n" +
                "    \"language\": \"" + newLanguage + "\"\n" +
                "}";

        // Act
        mockMvc.perform(put("/api/v1/places/{placeId}", place.getId()).contentType(MediaType.APPLICATION_JSON).content(requestBodyJson))
                .andExpect(status().isNoContent());
        Place updatedPlace = placeRepository.findById(place.getId()).orElseThrow();
        Place expectedPlace = new Place(place.getId(), newPlaceSlug, newLat, newLon, newPlaceName, newTimezone, newLanguage, null);

        // Assert
        assertThat(updatedPlace).isEqualTo(expectedPlace);
    }

    @Test
    @DisplayName("Method: PUT, Endpoint: /api/v1/places/{placeId} should update throw 400, because request body is incorrect")
    @WithMockUser(authorities = {"update_place"})
    public void updatePlaceByIdUnSuccessfulTest() throws Exception {
        // Arrange
        String newPlaceName = "newPlaceName";
        String newPlaceSlug = "newSlug";
        String newTimezone = "newTimezone";
        double lat = 50D;
        String newLanguage = "ru2";
        String requestBodyJson = "{\n" +
                "    \"name\": \"" + newPlaceName + "\",\n" +
                "    \"slug\": \"" + newPlaceSlug + "\",\n" +
                "    \"lat\": " + lat + ",\n" +
                "    \"timezone\": \"" + newTimezone + "\",\n" +
                "    \"language\": \"" + newLanguage + "\"\n" +
                "}";
        String expectedErrorMessage = "org.springframework.validation.BeanPropertyBindingResult: 1 errors\n" +
                "Field error in object 'updatePlacePayload' on field 'lon': rejected value [null]; codes [NotNull.updatePlacePayload.lon,NotNull.lon,NotNull.java.lang.Double,NotNull]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [updatePlacePayload.lon,lon]; arguments []; default message [lon]]; default message [must not be null]";

        // Act
        String response = mockMvc.perform(put("/api/v1/places/0").contentType(MediaType.APPLICATION_JSON).content(requestBodyJson))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();
        ExceptionMessage exceptionMessage = objectMapper.readValue(response, ExceptionMessage.class);

        // Assert
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(exceptionMessage.status()).isEqualTo(400);
        softly.assertThat(exceptionMessage.error()).isEqualTo(expectedErrorMessage);
        softly.assertThat(exceptionMessage.path()).isEqualTo("/api/v1/places/0");

        softly.assertAll();
    }

    @Test
    @DisplayName("Method: DELETE, Endpoint: /api/v1/places/{placeId} should delete place in db")
    @WithMockUser(authorities = {"remove_place"})
    public void deletePlaceByIdTest() throws Exception {
        // Arrange
        Place place = new Place(null, "place", 50D, 60D, "name1", "timezone1", "ru1", null);
        place = placeRepository.save(place);

        // Act
        mockMvc.perform(delete("/api/v1/places/{placeId}", place.getId()))
                .andExpect(status().isNoContent());

        // Assert
        assertThat(placeRepository.findById(place.getId())).isEmpty();
    }

    @Test
    @DisplayName("Method: GET, Endpoint: /api/v1/places/{placeId} should return 403 because user do not have authority read_place")
    @WithMockUser
    public void findPlaceByIdUnAuthorizedTest() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(get("/api/v1/places/1"))
                .andExpect(status().is(403));
    }

    @Test
    @DisplayName("Method: POST, Endpoint: /api/v1/places should return 403 because user do not have authority create_place")
    @WithMockUser
    public void createPlaceUnAuthorizedTest() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(post("/api/v1/places").content("{}").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(403));
    }

    @Test
    @DisplayName("Method: PUT, Endpoint: /api/v1/places/{placeId} should return 403 because user do not have authority update_place")
    @WithMockUser
    public void updatePlaceByIdUnAuthorizedTest() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(put("/api/v1/places/5").content("{}").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(403));
    }

    @Test
    @DisplayName("Method: DELETE, Endpoint: /api/v1/places/{placeId} should return 403 because user do not have authority remove_place")
    @WithMockUser
    public void deletePlaceByIdUnAuthorizedTest() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(delete("/api/v1/places/5"))
                .andExpect(status().is(403));
    }
}
