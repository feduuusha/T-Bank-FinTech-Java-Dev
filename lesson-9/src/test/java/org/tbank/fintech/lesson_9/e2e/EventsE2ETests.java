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
import org.tbank.fintech.lesson_9.entity.Event;
import org.tbank.fintech.lesson_9.entity.Place;
import org.tbank.fintech.lesson_9.exception.ExceptionMessage;
import org.tbank.fintech.lesson_9.repository.EventRepository;
import org.tbank.fintech.lesson_9.repository.PlaceRepository;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
public class EventsE2ETests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private EventRepository eventRepository;
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
        eventRepository.deleteAll();
        placeRepository.deleteAll();
    }

    @Test
    @DisplayName("Method: GET, Endpoint: /api/v1/events should return all events from db")
    @WithMockUser(authorities = {"read_event"})
    public void findAllEventsTest() throws Exception {
        // Arrange
        Place place = new Place(null, "place", 50D, 60D, "name1", "timezone1", "ru1", null);
        place = placeRepository.save(place);
        List<Event> eventsInRepo = List.of(
          new Event(null, "name1", LocalDateTime.of(2024, 12, 12, 12, 12, 12), place, "description1"),
          new Event(null, "name2", LocalDateTime.of(2023, 11, 12, 12, 12, 12), place, "description2"),
          new Event(null, "name3", LocalDateTime.of(2022, 10, 12, 12, 12, 12), place, "description3")
        );
        eventsInRepo = eventRepository.saveAll(eventsInRepo);

        // Act
        // Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/events"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(eventsInRepo)));
    }

    @Test
    @DisplayName("Method: GET, Endpoint: /api/v1/events?dateFrom&dateTo&name&place should return events from db by filter")
    @WithMockUser(authorities = {"read_event"})
    public void findAllEventsByFilterTest() throws Exception {
        // Arrange
        Place place = new Place(null, "place", 50D, 60D, "name1", "timezone1", "ru1", null);
        Place place2 = new Place(null, "place2", 50D, 60D, "name2", "timezone2", "ru2", null);
        place = placeRepository.save(place);
        place2 = placeRepository.save(place2);
        List<Event> eventsInRepo = List.of(
                new Event(null, "name1", LocalDateTime.of(2024, 12, 12, 12, 12, 12), place, "description1"),
                new Event(null, "name2", LocalDateTime.of(2023, 11, 12, 12, 12, 12), place, "description2"),
                new Event(null, "name1", LocalDateTime.of(2022, 10, 12, 12, 12, 12), place, "description3"),
                new Event(null, "name1", LocalDateTime.of(2022, 12, 12, 12, 12, 12), place2, "description4"),
                new Event(null, "name2", LocalDateTime.of(2023, 11, 12, 12, 12, 12), place2, "description5"),
                new Event(null, "name1", LocalDateTime.of(2024, 10, 12, 12, 12, 12), place2, "description6")
        );
        eventsInRepo = eventRepository.saveAll(eventsInRepo);

        // Act
        // Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/events?name=name1&fromDate=01-01-2023 00:00&toDate=01-01-2025 00:00&place=name2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(eventsInRepo.get(5)))));
    }

    @Test
    @DisplayName("Method: POST, Endpoint: /api/v1/events should save event in db")
    @WithMockUser(authorities = {"create_event"})
    public void createEventTest() throws Exception {
        // Arrange
        Place place = new Place(null, "place", 50D, 60D, "name1", "timezone1", "ru1", null);
        place = placeRepository.save(place);
        String eventName = "name";
        LocalDateTime eventDate = LocalDateTime.of(2024, 1, 1, 12, 0);
        String description = "description";
        String requestBodyJson = "{\n" +
                "    \"name\": \"" + eventName + "\",\n" +
                "    \"date\": \"" + eventDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")) + "\",\n" +
                "    \"placeId\":" + place.getId() + ",\n" +
                "    \"description\": \"" + description + "\"\n" +
                "}";

        // Act
        String response = mockMvc.perform(post("/api/v1/events").content(requestBodyJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", MatchesPattern.matchesPattern(".*/api/v1/events/\\d+$")))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        Event event = objectMapper.readValue(response, Event.class);
        Event expectedEvent = new Event(event.getId(), eventName, eventDate, place, description);
        // Assert
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(event).isEqualTo(expectedEvent);
        softly.assertThat(eventRepository.findById(event.getId()).orElseThrow()).isEqualTo(expectedEvent);

        softly.assertAll();
    }

    @Test
    @DisplayName("Method: POST, Endpoint: /api/v1/events should throw 400 status, because body is incorrect (placeId is not provided)")
    @WithMockUser(authorities = {"create_event"})
    public void createEventUnSuccessfulTest() throws Exception {
        // Arrange
        String eventName = "name";
        LocalDateTime eventDate = LocalDateTime.of(2024, 1, 1, 12, 0);
        String description = "description";
        String requestBodyJson = "{\n" +
                "    \"name\": \"" + eventName + "\",\n" +
                "    \"date\": \"" + eventDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")) + "\",\n" +
                "    \"description\": \"" + description + "\"\n" +
                "}";
        String expectedErrorMessage = "org.springframework.validation.BeanPropertyBindingResult: 1 errors\n" +
                "Field error in object 'newEventPayload' on field 'placeId': rejected value [null]; codes [NotNull.newEventPayload.placeId,NotNull.placeId,NotNull.java.lang.Long,NotNull]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [newEventPayload.placeId,placeId]; arguments []; default message [placeId]]; default message [must not be null]";

        // Act
        String response = mockMvc.perform(post("/api/v1/events").content(requestBodyJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        ExceptionMessage exceptionMessage = objectMapper.readValue(response, ExceptionMessage.class);
        // Assert
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(exceptionMessage.status()).isEqualTo(400);
        softly.assertThat(exceptionMessage.error()).isEqualTo(expectedErrorMessage);
        softly.assertThat(exceptionMessage.path()).isEqualTo("/api/v1/events");

        softly.assertAll();
    }

    @Test
    @DisplayName("Method: GET, Endpoint: /api/v1/events/{eventId} should return event with specified id")
    @WithMockUser(authorities = {"read_event"})
    public void findEventByIdTest() throws Exception {
        // Arrange
        Place place = new Place(null, "place", 50D, 60D, "name1", "timezone1", "ru1", null);
        place = placeRepository.save(place);
        Event event = new Event(null, "name", LocalDateTime.of(2024, 1,1, 12, 0), place, "desc");
        event = eventRepository.save(event);


        // Act
        // Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/events/{eventId}", event.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(event)));
    }

    @Test
    @DisplayName("Method: GET, Endpoint: /api/v1/events/{eventId} should throw 404 because id is do not exist")
    @WithMockUser(authorities = {"read_event"})
    public void findEventByIdUnSuccessfulTest() throws Exception {
        // Arrange
        Long eventId = 1000L;
        // Act
        String response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/events/{eventId}", eventId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        ExceptionMessage exceptionMessage = objectMapper.readValue(response, ExceptionMessage.class);

        // Assert
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(exceptionMessage.status()).isEqualTo(404);
        softly.assertThat(exceptionMessage.path()).isEqualTo("/api/v1/events/" + eventId);
        softly.assertThat(exceptionMessage.error()).isEqualTo("Event with id: " + eventId + " not found");

        softly.assertAll();
    }

    @Test
    @DisplayName("Method: PUT, Endpoint: /api/v1/events/{eventId} should update event in db")
    @WithMockUser(authorities = {"update_event"})
    public void updateEventByIdTest() throws Exception {
        // Arrange
        Place place = new Place(null, "place", 50D, 60D, "name1", "timezone1", "ru1", null);
        place = placeRepository.save(place);
        Place newPlace = new Place(null, "newPlace", 55D, 65D, "newName", "newTz", "ru2", null);
        newPlace = placeRepository.save(newPlace);
        Event event = new Event(null, "name", LocalDateTime.of(2024, 1,1, 12, 0), place, "desc");
        event = eventRepository.save(event);
        String newEventName = "newEventName";
        LocalDateTime newEventDate = LocalDateTime.of(2007, 10, 9, 8, 30);
        String newEventDescription = "newEventDescription";
        String requestBodyJson = "{\n" +
                "    \"name\": \"" + newEventName + "\",\n" +
                "    \"date\": \"" + newEventDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")) + "\",\n" +
                "    \"placeId\":" + newPlace.getId() + ",\n" +
                "    \"description\": \"" + newEventDescription + "\"\n" +
                "}";

        // Act
        mockMvc.perform(put("/api/v1/events/{eventId}", event.getId()).contentType(MediaType.APPLICATION_JSON).content(requestBodyJson))
                .andExpect(status().isNoContent());
        Event updatedEvent = eventRepository.findById(event.getId()).orElseThrow();
        Event expectedEvent = new Event(event.getId(), newEventName, newEventDate, newPlace, newEventDescription);

        // Assert
        assertThat(updatedEvent).isEqualTo(expectedEvent);
    }

    @Test
    @DisplayName("Method: PUT, Endpoint: /api/v1/events/{eventId} should throw 404, because place in request body don t exist")
    @WithMockUser(authorities = {"update_event"})
    public void updateEventByIdUnSuccessful400Test() throws Exception {
        // Arrange
        Place place = new Place(null, "place", 50D, 60D, "name1", "timezone1", "ru1", null);
        place = placeRepository.save(place);
        Event event = new Event(null, "name", LocalDateTime.of(2024, 1,1, 12, 0), place, "desc");
        event = eventRepository.save(event);
        long placeId = 1000L;
        String newEventName = "newEventName";
        LocalDateTime newEventDate = LocalDateTime.of(2007, 10, 9, 8, 30);
        String newEventDescription = "newEventDescription";
        String requestBodyJson = "{\n" +
                "    \"name\": \"" + newEventName + "\",\n" +
                "    \"date\": \"" + newEventDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")) + "\",\n" +
                "    \"placeId\":" + placeId + ",\n" +
                "    \"description\": \"" + newEventDescription + "\"\n" +
                "}";

        // Act
        String response = mockMvc.perform(put("/api/v1/events/{eventId}", event.getId()).contentType(MediaType.APPLICATION_JSON).content(requestBodyJson))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();
        ExceptionMessage exceptionMessage = objectMapper.readValue(response, ExceptionMessage.class);

        // Assert
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(exceptionMessage.status()).isEqualTo(400);
        softly.assertThat(exceptionMessage.path()).isEqualTo("/api/v1/events/" + event.getId());
        softly.assertThat(exceptionMessage.error()).isEqualTo("Place with id: " + placeId + " does not exist");

        softly.assertAll();
    }

    @Test
    @DisplayName("Method: PUT, Endpoint: /api/v1/events/{eventId} should throw 400 status, because body is incorrect (placeId is not provided)")
    @WithMockUser(authorities = {"update_event"})
    public void updateEventByIdUnSuccessfulTest() throws Exception {
        // Arrange
        String newEventName = "newEventName";
        LocalDateTime newEventDate = LocalDateTime.of(2007, 10, 9, 8, 30);
        String newEventDescription = "newEventDescription";
        String requestBodyJson = "{\n" +
                "    \"name\": \"" + newEventName + "\",\n" +
                "    \"date\": \"" + newEventDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")) + "\",\n" +
                "    \"description\": \"" + newEventDescription + "\"\n" +
                "}";
        String expectedErrorMessage = "org.springframework.validation.BeanPropertyBindingResult: 1 errors\n" +
                "Field error in object 'updateEventPayload' on field 'placeId': rejected value [null]; codes [NotNull.updateEventPayload.placeId,NotNull.placeId,NotNull.java.lang.Long,NotNull]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [updateEventPayload.placeId,placeId]; arguments []; default message [placeId]]; default message [must not be null]";

        // Act
        String response = mockMvc.perform(put("/api/v1/events/0").contentType(MediaType.APPLICATION_JSON).content(requestBodyJson))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();
        ExceptionMessage exceptionMessage = objectMapper.readValue(response, ExceptionMessage.class);
        // Assert
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(exceptionMessage.status()).isEqualTo(400);
        softly.assertThat(exceptionMessage.error()).isEqualTo(expectedErrorMessage);
        softly.assertThat(exceptionMessage.path()).isEqualTo("/api/v1/events/0");

        softly.assertAll();
    }

    @Test
    @DisplayName("Method: DELETE, Endpoint: /api/v1/events/{eventId} should delete event in db")
    @WithMockUser(authorities = {"remove_event"})
    public void deleteEventByIdTest() throws Exception {
        // Arrange
        Place place = new Place(null, "place", 50D, 60D, "name1", "timezone1", "ru1", null);
        place = placeRepository.save(place);
        Event event = new Event(null, "name", LocalDateTime.of(2024, 1,1, 12, 0), place, "desc");
        event = eventRepository.save(event);

        // Act
        mockMvc.perform(delete("/api/v1/events/{eventId}", event.getId()))
                .andExpect(status().isNoContent());

        // Assert
        assertThat(eventRepository.findById(event.getId())).isEmpty();
    }

    @Test
    @DisplayName("Method: GET, Endpoint: /api/v1/events/{eventId} should return 403 because user do not have authority read_event")
    @WithMockUser
    public void findEventByIdUnAuthorizedTest() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(get("/api/v1/events/1"))
                .andExpect(status().is(403));
    }

    @Test
    @DisplayName("Method: POST, Endpoint: /api/v1/events should return 403 because user do not have authority create_event")
    @WithMockUser
    public void createEventUnAuthorizedTest() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(post("/api/v1/events").content("{}").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(403));
    }

    @Test
    @DisplayName("Method: PUT, Endpoint: /api/v1/events/{eventId} should return 403 because user do not have authority update_event")
    @WithMockUser
    public void updateEventByIdUnAuthorizedTest() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(put("/api/v1/events/5").content("{}").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(403));
    }

    @Test
    @DisplayName("Method: DELETE, Endpoint: /api/v1/events/{eventId} should return 403 because user do not have authority remove_event")
    @WithMockUser
    public void deleteEventByIdUnAuthorizedTest() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(delete("/api/v1/events/5"))
                .andExpect(status().is(403));
    }
}
