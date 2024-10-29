package org.tbank.fintech.service;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tbank.fintech.entity.Coords;
import org.tbank.fintech.entity.Location;
import org.tbank.fintech.entity.memento.LocationMemento;
import org.tbank.fintech.repository.LocationRepository;
import org.tbank.fintech.service.impl.LocationServiceImpl;
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
 * Test class for the {@link LocationServiceImpl}
 */
@ExtendWith(MockitoExtension.class)
public class LocationServiceImplTest {

    @Mock
    private LocationRepository locationRepository;
    @Mock
    private Caretaker<Long, LocationMemento> locationsCaretaker;
    @InjectMocks
    private LocationServiceImpl locationService;

    @Test
    public void findAllLocations_repositoryContainsThreeLocation_shouldReturnListOfThreeLocations() {
        // Arrange
        List<Location> repoLocations= List.of(
                new Location("slug1", "name1", "timezone1", new Coords(1D, 2D), "language1"),
                new Location("slug2", "name2", "timezone2", new Coords(3D, 4D), "language2"),
                new Location("slug3", "name3", "timezone3", new Coords(5D, 6D), "language3")
        );
        when(locationRepository.findAll()).thenReturn(repoLocations);

        // Act
        List<Location> locations = locationService.findAllLocations();

        // Assert
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(locations.size()).isEqualTo(repoLocations.size());
        for (int i = 0; i < repoLocations.size(); ++i) {
            softly.assertThat(repoLocations.get(i)).isEqualTo(locations.get(i));
        }

        softly.assertAll();
    }

    @Test
    public void findLocationById_repositoryContainsLocation_shouldReturnLocation() {
        // Arrange
        Location repoLocation = new Location("slug1", "name1", "timezone1", new Coords(1D, 2D), "language1");
        Long locationId = 1L;
        repoLocation.setId(locationId);
        when(locationRepository.findById(locationId)).thenReturn(Optional.of(repoLocation));

        // Act
        Location location = locationService.findLocationById(locationId);

        // Assert
        assertThat(location).isEqualTo(repoLocation);
    }

    @Test
    public void findLocationById_repositoryNotContainsLocation_shouldThrowNoSuchElementException() {
        // Arrange
        Long locationId = 1L;
        when(locationRepository.findById(locationId)).thenReturn(Optional.empty());

        // Act
        // Assert
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(
                () -> locationService.findLocationById(locationId)
        ).withMessage("Location with id=" + locationId + " was not found");
    }

    @Test
    public void createLocation_shouldCreateLocation() {
        // Arrange
        String slug = "slug";
        String name = "name";
        String timezone = "timezone";
        Coords coords = new Coords(10D,20D);
        String language = "language";
        Location locationInit = new Location(slug, name, timezone, coords, language);
        when(locationRepository.save(locationInit)).thenReturn(locationInit);

        // Act
        locationService.createLocation(slug, name, timezone, coords, language);

        // Assert
        ArgumentCaptor<Location> locationArgumentCaptor = ArgumentCaptor.forClass(Location.class);
        verify(locationRepository).save(locationArgumentCaptor.capture());
        Location location = locationArgumentCaptor.getValue();
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(location.getName()).isEqualTo(name);
        softly.assertThat(location.getSlug()).isEqualTo(slug);
        softly.assertThat(location.getTimezone()).isEqualTo(timezone);
        softly.assertThat(location.getCoords()).isEqualTo(coords);
        softly.assertThat(location.getLanguage()).isEqualTo(language);

        softly.assertAll();
    }

    @Test
    public void updateLocation_shouldUpdateLocation() {
        // Arrange
        Location repoLocation = new Location("slug1", "name1", "timezone1", new Coords(1D, 2D), "language1");
        Long locationId = 1L;
        repoLocation.setId(locationId);
        String expectedName = "new_name";
        String expectedSlug = "new_slug";
        String expectedTimezone = "new_timezone";
        Coords expectedCoords = new Coords(10D, 20D);
        String expectedLanguage = "new_language";
        when(locationsCaretaker.get(locationId)).thenReturn(new ArrayList<>());
        when(locationRepository.findById(locationId)).thenReturn(Optional.of(repoLocation));

        // Act
        locationService.updateLocation(locationId, expectedSlug, expectedName, expectedTimezone, expectedCoords, expectedLanguage);

        // Assert
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(repoLocation.getId()).isEqualTo(locationId);
        softly.assertThat(repoLocation.getName()).isEqualTo(expectedName);
        softly.assertThat(repoLocation.getSlug()).isEqualTo(expectedSlug);
        softly.assertThat(repoLocation.getTimezone()).isEqualTo(expectedTimezone);
        softly.assertThat(repoLocation.getCoords()).isEqualTo(expectedCoords);
        softly.assertThat(repoLocation.getLanguage()).isEqualTo(expectedLanguage);

        softly.assertAll();
    }

    @Test
    public void deleteLocationById_shouldDeleteLocation() {
        // Arrange
        Long locationId = 5L;

        // Act
        locationService.deleteLocationById(locationId);

        // Assert
        verify(locationRepository).deleteById(locationId);
    }

    @Test
    @DisplayName("findAllVersionsOfLocationById should return list of location mementos because location with provided id is exist")
    public void findAllVersionsOfLocationByIdTest() {
        // Arrange
        Long locationId = 5L;
        List<LocationMemento> mementos = List.of(
                new LocationMemento("slug1", "name1", "timezone1", new Coords(10D, 20D), "language1"),
                new LocationMemento("slug2", "name2", "timezone2", new Coords(20D, 10D), "language2"),
                new LocationMemento("slug3", "name3", "timezone3", new Coords(30D, 30D), "language3")
        );
        when(locationsCaretaker.get(locationId)).thenReturn(mementos);

        // Act
        var list = locationService.findAllVersionsOfLocationById(locationId);

        // Assert
        assertThat(list).isEqualTo(mementos);
    }

    @Test
    @DisplayName("findAllVersionsOfLocationById should throw NoSuchElementException because location with provided id is not exist")
    public void findAllVersionsOfLocationByIdUnSuccessfulTest() {
        // Arrange
        Long locationId = 5L;
        when(locationsCaretaker.get(locationId)).thenReturn(null);

        // Act
        // Assert
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> locationService.findAllVersionsOfLocationById(locationId))
                .withMessage("Location with ID: 5 has never existed");
    }

    @Test
    @DisplayName("findVersionOfLocationByIndex should return location memento because location with provided id is exist and index is correct")
    public void findVersionOfLocationByIndexTest() {
        // Arrange
        Long locationId = 5L;
        int versionIndex = 1;
        List<LocationMemento> mementos = List.of(
                new LocationMemento("slug1", "name1", "timezone1", new Coords(10D, 20D), "language1"),
                new LocationMemento("slug2", "name2", "timezone2", new Coords(20D, 10D), "language2"),
                new LocationMemento("slug3", "name3", "timezone3", new Coords(30D, 30D), "language3")
        );
        when(locationsCaretaker.get(locationId)).thenReturn(mementos);

        // Act
        var memento = locationService.findVersionOfLocationByIndex(locationId, versionIndex);

        // Assert
        assertThat(memento).isEqualTo(mementos.get(versionIndex));
    }

    @Test
    @DisplayName("findVersionOfLocationByIndex should throw NoSuchElementException because index is incorrect")
    public void findVersionOfLocationByIndexUnSuccessfulTest() {
        // Arrange
        Long locationId = 5L;
        Integer versionIndex = 5;
        List<LocationMemento> mementos = List.of(
                new LocationMemento("slug1", "name1", "timezone1", new Coords(10D, 20D), "language1"),
                new LocationMemento("slug2", "name2", "timezone2", new Coords(20D, 10D), "language2"),
                new LocationMemento("slug3", "name3", "timezone3", new Coords(30D, 30D), "language3")
        );
        when(locationsCaretaker.get(locationId)).thenReturn(mementos);

        // Act
        // Assert
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> locationService.findVersionOfLocationByIndex(locationId, versionIndex))
                .withMessage("Location version with index " + versionIndex + " not found");
    }

    @Test
    @DisplayName("restoreVersionOfLocation should restore location by index of memento")
    public void restoreVersionOfLocationTest() {
        // Arrange
        Long locationId = 5L;
        int versionIndex = 2;
        List<LocationMemento> mementos = new ArrayList<>();
        mementos.add(new LocationMemento("slug1", "name1", "timezone1", new Coords(10D, 20D), "language1"));
        mementos.add(new LocationMemento("slug2", "name2", "timezone2", new Coords(20D, 10D), "language2"));
        mementos.add(new LocationMemento("slug3", "name3", "timezone3", new Coords(30D, 30D), "language3"));

        when(locationsCaretaker.get(locationId)).thenReturn(mementos);
        Location location = new Location(locationId, "slug", "name", "timezone", new Coords(50D, 50D), "language");
        when(locationRepository.findById(locationId)).thenReturn(Optional.of(location));

        // Act
        Location locationResult = locationService.restoreVersionOfLocation(locationId, versionIndex);

        // Assert
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(locationResult.getSlug()).isEqualTo(mementos.get(versionIndex).slug());
        softly.assertThat(locationResult.getName()).isEqualTo(mementos.get(versionIndex).name());
        softly.assertThat(locationResult.getTimezone()).isEqualTo(mementos.get(versionIndex).timezone());
        softly.assertThat(locationResult.getCoords()).isEqualTo(mementos.get(versionIndex).coords());
        softly.assertThat(locationResult.getLanguage()).isEqualTo(mementos.get(versionIndex).language());

        softly.assertAll();
    }
}
