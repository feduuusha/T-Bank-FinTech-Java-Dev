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
}
