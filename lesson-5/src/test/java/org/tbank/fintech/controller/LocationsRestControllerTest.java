package org.tbank.fintech.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.tbank.fintech.entity.Coords;
import org.tbank.fintech.entity.Location;
import org.tbank.fintech.entity.memento.LocationMemento;
import org.tbank.fintech.service.LocationService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the {@link LocationsRestController}
 */
@WebMvcTest(controllers = {LocationsRestController.class})
public class LocationsRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LocationService locationService;

    @Test
    @DisplayName("Controller create location test, method:post endpoint:/api/v1/locations")
    public void createLocationTest() throws Exception {
        // given
        String payload = """
                {
                    "slug": "orn",
                    "name": "ORENBURG",
                    "timezone": "GMT+03:00",
                    "coords": {
                        "lat": 56.32688699999997,
                        "lon": 44.00598599999999
                    },
                    "language": "ru"
                }""";
        Coords coords = new Coords(56.32688699999997, 44.00598599999999);
        Location testLocation = new Location(0L, "orn", "ORENBURG", "GMT+03:00", coords, "ru");
        when(locationService.createLocation("orn", "ORENBURG", "GMT+03:00", coords,  "ru")).thenReturn(testLocation);

        // when
        mockMvc.perform(post("/api/v1/locations")
                        .content(payload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/v1/locations/0"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("{\"id\":0,\"slug\":\"orn\",\"name\":\"ORENBURG\",\"timezone\":\"GMT+03:00\",\"coords\":{\"lat\":56.32688699999997,\"lon\":44.00598599999999},\"language\":\"ru\"}"));
    }

    @Test
    @DisplayName("Controller delete location by id test, method:delete endpoint:/api/v1/locations/{locationId}")
    public void deleteLocationByIdTest() throws Exception {
        // given
        Long locationId = 0L;

        // when
        mockMvc.perform(delete("/api/v1/locations/{locationId}", locationId))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Controller find all locations test, method:get endpoint:/api/v1/locations")
    public void findAllLocationsTest() throws Exception {
        // given
        List<Location> locations = List.of(
                new Location("plug", "name", "timezone", new Coords(50D, 40D), "en"),
                new Location("like", "mail", "cat", new Coords(50D, 40D), "ru")
        );
        when(locationService.findAllLocations()).thenReturn(locations);

        //when
        mockMvc.perform(get("/api/v1/locations"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("[{\"id\":null,\"slug\":\"plug\",\"name\":\"name\",\"timezone\":\"timezone\",\"coords\":{\"lat\":50.0,\"lon\":40.0},\"language\":\"en\"},{\"id\":null,\"slug\":\"like\",\"name\":\"mail\",\"timezone\":\"cat\",\"coords\":{\"lat\":50.0,\"lon\":40.0},\"language\":\"ru\"}]"));
    }

    @Test
    @DisplayName("Controller find location by id test, method:get endpoint:/api/v1/locations/{locationId}")
    public void findLocationByIdTest() throws Exception {
        // given
        Long locationId = 0L;
        Location testLocation = new Location(locationId, "org", "tbank", "GMT: 00:00", new Coords(77D, 99D), "es");
        when(locationService.findLocationById(locationId)).thenReturn(testLocation);

        // then
        mockMvc.perform(get("/api/v1/locations/{locationId}", locationId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("{\"id\":0,\"slug\":\"org\",\"name\":\"tbank\",\"timezone\":\"GMT: 00:00\",\"coords\":{\"lat\":77.0,\"lon\":99.0},\"language\":\"es\"}"));
    }

    @Test
    @DisplayName("Controller update location by id, method:put endpoint:/api/v1/locations/{locationId}")
    public void updateLocationByIdTest() throws Exception {
        // given
        String payload = """
                {
                    "slug": "orn",
                    "name": "ORENBURG",
                    "timezone": "GMT+03:00",
                    "coords": {
                        "lat": 56.32688699999997,
                        "lon": 44.00598599999999
                    },
                    "language": "ru"
                }""";
        Long locationId = 0L;
        Location testLocation = new Location(locationId,"org", "tbank", "GMT: 00:00", new Coords(77D, 99D), "es");
        when(locationService.findLocationById(locationId)).thenReturn(testLocation);

        // then
        mockMvc.perform(put("/api/v1/locations/{locationId}", locationId).content(payload).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Method: GET Endpoint:/api/v1/locations/{locationId}/versions should return list of mementos because location with location id is exist")
    public void findAllVersionsOfLocationTest() throws Exception {
        // Arrange
        Long locationId = 5L;
        List<LocationMemento> mementos = List.of(
                new LocationMemento("slug1", "name1", "timezone1", new Coords(40D, 50D), "language1"),
                new LocationMemento("slug2", "name2", "timezone2", new Coords(50D, 60D), "language2"),
                new LocationMemento("slug3", "name3", "timezone3", new Coords(60D, 70D), "language3")
        );
        when(locationService.findAllVersionsOfLocationById(locationId)).thenReturn(mementos);

        // Act
        // Assert
        mockMvc.perform(get("/api/v1/locations/{locationId}/versions", locationId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[{\"slug\":\"slug1\",\"name\":\"name1\",\"timezone\":\"timezone1\",\"coords\":{\"lat\":40.0,\"lon\":50.0},\"language\":\"language1\"},{\"slug\":\"slug2\",\"name\":\"name2\",\"timezone\":\"timezone2\",\"coords\":{\"lat\":50.0,\"lon\":60.0},\"language\":\"language2\"},{\"slug\":\"slug3\",\"name\":\"name3\",\"timezone\":\"timezone3\",\"coords\":{\"lat\":60.0,\"lon\":70.0},\"language\":\"language3\"}]"));
    }

    @Test
    @DisplayName("Method: GET Endpoint:/api/v1/locations/{locationId}/versions/{versionIndex} should return memento because location with provided id is exist and memento with provided index exist")
    public void findVersionOfLocationByIndexTest() throws Exception {
        // Arrange
        Long locationId = 5L;
        Integer versionIndex = 1;
        var memento = new LocationMemento("slug2", "name2", "timezone2", new Coords(50D, 60D), "language2");
        when(locationService.findVersionOfLocationByIndex(locationId, versionIndex)).thenReturn(memento);

        // Act
        // Assert
        mockMvc.perform(get("/api/v1/locations/{locationId}/versions/{versionIndex}", locationId, versionIndex))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"slug\":\"slug2\",\"name\":\"name2\",\"timezone\":\"timezone2\",\"coords\":{\"lat\":50.0,\"lon\":60.0},\"language\":\"language2\"}"));
    }

    @Test
    @DisplayName("Method: POST Endpoint:/api/v1/locations/{locationId}/restore/{versionIndex} should restore locationMemento with provided index in location with provided id and return location")
    public void restoreVersionOfLocationTest() throws Exception {
        // Arrange
        Long locationId = 5L;
        Integer versionIndex = 1;
        var restoredLocation = new Location("slug2", "name2", "timezone2", new Coords(50D, 60D), "language2");
        when(locationService.restoreVersionOfLocation(locationId, versionIndex)).thenReturn(restoredLocation);

        // Act
        // Assert
        mockMvc.perform(post("/api/v1/locations/{locationId}/restore/{versionIndex}", locationId, versionIndex))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"slug\":\"slug2\",\"name\":\"name2\",\"timezone\":\"timezone2\",\"coords\":{\"lat\":50.0,\"lon\":60.0},\"language\":\"language2\"}"));
    }
}
