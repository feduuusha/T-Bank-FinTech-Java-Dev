package org.tbank.fintech.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.tbank.fintech.entity.Coords;
import org.tbank.fintech.entity.Location;
import org.tbank.fintech.repository.LocationRepository;
import org.tbank.fintech.service.LocationService;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;

    @Override
    public List<Location> findAllLocations() {
        return this.locationRepository.findAll();
    }

    @Override
    public Location createLocation(String slug, String name, String timezone, Coords coords, String language) {
        return this.locationRepository.save(new Location(slug, name, timezone, coords, language));
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
    }

    @Override
    public void deleteLocationById(Long locationId) {
        this.locationRepository.deleteById(locationId);
    }
}
