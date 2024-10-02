package org.tbank.fintech.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import org.tbank.fintech.controller.payload.location.NewLocationPayload;
import org.tbank.fintech.controller.payload.location.UpdateLocationPayload;
import org.tbank.fintech.entity.Location;
import org.tbank.fintech.executor_timer_starter.execution_timer.ExecutionTimer;
import org.tbank.fintech.service.LocationService;

import java.util.List;

@ExecutionTimer
@RestController
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
public class LocationsRestController {

    private final LocationService locationService;

    @GetMapping
    public List<Location> findAllLocations() {
        return this.locationService.findAllLocations();
    }

    @PostMapping
    public ResponseEntity<Location> createLocation(@Valid @RequestBody NewLocationPayload payload,
                                                   BindingResult bindingResult,
                                                   UriComponentsBuilder uriComponentsBuilder) throws BindException {
        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            } else {
                throw new BindException(bindingResult);
            }
        } else {
            Location location = this.locationService.createLocation(payload.slug(), payload.name(), payload.timezone(), payload.coords(), payload.language());
            return ResponseEntity
                    .created(uriComponentsBuilder
                            .replacePath("/api/v1/locations/{locationId}")
                            .build(location.getId()))
                    .body(location);
        }
    }

    @GetMapping("/{locationId:\\d+}")
    public Location findLocationById(@PathVariable Long locationId) {
        return this.locationService.findLocationById(locationId);
    }

    @PutMapping("/{locationId:\\d+}")
    public ResponseEntity<Void> updateLocation(@PathVariable Long locationId,
                                               @Valid @RequestBody UpdateLocationPayload payload,
                                               BindingResult bindingResult) throws BindException {
        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            } else {
                throw new BindException(bindingResult);
            }
        } else {
            this.locationService.updateLocation(locationId, payload.slug(), payload.name(), payload.timezone(), payload.coords(), payload.language());
            return ResponseEntity.noContent().build();
        }
    }

    @DeleteMapping("/{locationId:\\d+}")
    public ResponseEntity<Void> deleteLocationById(@PathVariable Long locationId) {
        this.locationService.deleteLocationById(locationId);
        return ResponseEntity.noContent().build();
    }

}
