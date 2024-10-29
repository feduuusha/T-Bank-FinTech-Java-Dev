package org.tbank.fintech.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.tbank.fintech.clients.LocationsRestClient;
import org.tbank.fintech.entity.Location;
import org.tbank.fintech.repository.LocationRepository;

import java.util.HashSet;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LocationInitializationCommand implements Command<Void> {
    private final LocationsRestClient locationsRestClient;
    private final LocationRepository locationRepository;
    @Override
    public Void call() {
        List<Location> locations = locationsRestClient.findAllLocations("ru", "slug", List.of("slug", "name", "timezone", "coords", "language"));
        HashSet<String> slugs = new HashSet<>();
        for (Location location : locationRepository.findAll()) {
            slugs.add(location.getSlug());
        }
        for (Location location : locations) {
            if (!slugs.contains(location.getSlug())) {
                locationRepository.save(location);
            }
        }
        return null;
    }

    @Override
    public String getType() {
        return "initLocations";
    }
}
