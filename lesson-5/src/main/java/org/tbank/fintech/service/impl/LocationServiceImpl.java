package org.tbank.fintech.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.tbank.fintech.entity.Coords;
import org.tbank.fintech.entity.Location;
import org.tbank.fintech.entity.memento.LocationMemento;
import org.tbank.fintech.repository.LocationRepository;
import org.tbank.fintech.service.LocationService;
import org.tbank.fintech.util.Caretaker;

import java.util.*;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;
    private final Caretaker<Long, LocationMemento> locationsCaretaker;

    @Override
    public List<Location> findAllLocations() {
        return this.locationRepository.findAll();
    }

    @Override
    public Location createLocation(String slug, String name, String timezone, Coords coords, String language) {
        var location =  this.locationRepository.save(new Location(slug, name, timezone, coords, language));
        var locationCaretaker = new ArrayList<LocationMemento>();
        locationCaretaker.add(location.createMemento());
        locationsCaretaker.put(location.getId(), locationCaretaker);
        return location;
    }

    @Override
    public Location findLocationById(Long locationId) {
        return locationRepository.findById(locationId).orElseThrow(() -> new NoSuchElementException("Location with id=" + locationId + " was not found"));
    }

    @Override
    public void updateLocation(Long locationId, String slug, String name, String timezone, Coords coords, String language) {
        Location location = this.findLocationById(locationId);
        location.setSlug(slug);
        location.setName(name);
        location.setTimezone(timezone);
        location.setCoords(coords);
        location.setLanguage(language);
        this.locationRepository.updateById(locationId, location);
        locationsCaretaker.get(locationId).add(location.createMemento());
    }

    @Override
    public void deleteLocationById(Long locationId) {
        this.locationRepository.deleteById(locationId);
    }

    @Override
    public List<LocationMemento> findAllVersionsOfLocationById(Long locationId) {
        return Optional.ofNullable(locationsCaretaker.get(locationId)).orElseThrow(() -> new NoSuchElementException("Location with ID: " + locationId + " has never existed"));
    }

    @Override
    public LocationMemento findVersionOfLocationByIndex(Long locationId, Integer versionIndex) {
        var locationsCaretaker = findAllVersionsOfLocationById(locationId);
        try {
            return locationsCaretaker.get(versionIndex);
        } catch (IndexOutOfBoundsException exception) {
            throw new NoSuchElementException("Location version with index " + versionIndex + " not found");
        }
    }

    @Override
    public Location restoreVersionOfLocation(Long locationId, Integer versionIndex) {
        var location = findLocationById(locationId);
        var memento = findVersionOfLocationByIndex(locationId, versionIndex);
        location.restore(memento);
        locationsCaretaker.get(locationId).add(location.createMemento());
        return location;
    }
}
