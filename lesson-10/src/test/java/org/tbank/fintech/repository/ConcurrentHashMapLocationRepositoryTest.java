package org.tbank.fintech.repository;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tbank.fintech.entity.Coords;
import org.tbank.fintech.entity.Location;
import org.tbank.fintech.repository.impl.ConcurrentHashMapLocationRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for the {@link ConcurrentHashMapLocationRepository}
 */
class ConcurrentHashMapLocationRepositoryTest {

    private final LocationRepository locationRepository = new ConcurrentHashMapLocationRepository();

    @Test
    @DisplayName("findAllLocations should return all saved locations")
    void testFindAllLocations() {
        // given
        Location location1 = new Location("slug1", "name 1", "timezone 1", new Coords(50D, 40D), "ru");
        Location location2 = new Location("slug2", "name 2", "timezone 2", new Coords(60D, 50D), "en");
        locationRepository.save(location1);
        locationRepository.save(location2);

        // when
        List<Location> allLocations = locationRepository.findAll();

        // then
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(allLocations.size()).as(() -> "Expected 2 locations, but got " + allLocations.size()).isEqualTo(2);
        softly.assertThat(allLocations.contains(location1)).as(() -> "Expected location1 to be present in the list").isTrue();
        softly.assertThat(allLocations.contains(location2)).as(() -> "Expected location2 to be present in the list").isTrue();

        softly.assertAll();
    }

    @Test
    @DisplayName("saveLocation should save a new location and assign ID")
    void testSaveLocation() {
        // given
        Location location = new Location("slug1", "name 1", "timezone 1", new Coords(50D, 40D), "ru");

        // when
        Location savedLocation = locationRepository.save(location);

        // then
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(savedLocation.getId()).as(() -> "Expected ID to be 1, but got " + savedLocation.getId()).isEqualTo(1);
        softly.assertThat(savedLocation).as("Expected savedLocation to be equal to location").isEqualTo(location);
        softly.assertThat(locationRepository.findAll().contains(savedLocation)).as("Expected savedLocation to be present in the list").isTrue();

        softly.assertAll();
    }

    @Test
    @DisplayName("findLocationById should find a location by its ID")
    void testFindLocationById() {
        // given
        Location location = new Location("slug1", "name 1", "timezone 1", new Coords(50D, 40D), "ru");
        locationRepository.save(location);

        // when
        Optional<Location> foundLocation = locationRepository.findById(1L);

        // then
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(foundLocation.isPresent()).as("Expected location to be present").isTrue();
        softly.assertThat(foundLocation.get()).as("Expected foundLocation to be equal to location").isEqualTo(location);

        softly.assertAll();
    }

    @Test
    @DisplayName("findLocationById should return empty Optional if location not found")
    void testFindLocationByIdNotFound() {
        // given
        Optional<Location> foundLocation = locationRepository.findById(100L);

        // then
        assertThat(foundLocation.isPresent()).as(() ->"Expected location to be not present").isFalse();
    }

    @Test
    @DisplayName("deleteLocationById should delete a location by its ID")
    void testDeleteLocationById() {
        // given
        Location location = new Location("slug1", "name 1", "timezone 1", new Coords(50D, 40D), "ru");
        locationRepository.save(location);

        // when
        locationRepository.deleteById(1L);

        // then
        assertThat(locationRepository.findById(1L).isPresent()).as("Expected location to be deleted").isFalse();
    }
}