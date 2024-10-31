package org.tbank.fintech.service;

import org.tbank.fintech.entity.Coords;
import org.tbank.fintech.entity.Location;

import java.util.List;

public interface LocationService {
    List<Location> findAllLocations();

    Location createLocation(String slug, String name, String timezone, Coords coords, String language);

    Location findLocationById(Long locationId);

    void updateLocation(Long locationId, String slug, String name, String timezone, Coords coords, String language);

    void deleteLocationById(Long locationId);
}
