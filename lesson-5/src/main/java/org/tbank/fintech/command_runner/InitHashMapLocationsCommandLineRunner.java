package org.tbank.fintech.command_runner;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.tbank.fintech.clients.impl.RestClientLocationsRestClient;
import org.tbank.fintech.entity.Location;
import org.tbank.fintech.executor_timer_starter.execution_timer.ExecutionTimer;
import org.tbank.fintech.repository.impl.ConcurrentHashMapLocationRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class InitHashMapLocationsCommandLineRunner implements CommandLineRunner {

    private final RestClientLocationsRestClient locationsRestClient;
    private final ConcurrentHashMapLocationRepository locationRepository;


    @ExecutionTimer
    @Override
    public void run(String... args) {
        List<Location> locations = this.locationsRestClient.findAllLocations("ru", "slug", List.of("slug", "name", "timezone", "coords", "language"));
        for (Location location : locations) {
            this.locationRepository.save(location);
        }
    }
}
