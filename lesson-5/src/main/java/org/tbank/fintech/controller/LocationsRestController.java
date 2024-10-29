package org.tbank.fintech.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import org.tbank.fintech.controller.payload.location.NewLocationPayload;
import org.tbank.fintech.controller.payload.location.UpdateLocationPayload;
import org.tbank.fintech.entity.Location;
import org.tbank.fintech.entity.memento.LocationMemento;
import org.tbank.fintech.executor_timer_starter.execution_timer.ExecutionTimer;
import org.tbank.fintech.service.LocationService;

import java.util.List;

@Tag(name = "Locations REST Controller", description = "Controller for CRUD operations with locations and for restore versions of locations")
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
            throw new BindException(bindingResult);
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
            throw new BindException(bindingResult);
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

    @Operation(description = "Get all versions of specific location by its id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Return all versions of location when location with specified id is exist", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = LocationMemento.class)))),
                    @ApiResponse(responseCode = "404", description = "Return when location with specified id is not exist", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "400", description = "Return when request param 'locationId' is not specified or it is null", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class))),
            })
    @GetMapping("/{locationId:\\d+}/versions")
    public List<LocationMemento> findAllVersionsOfLocation(@PathVariable Long locationId) {
        return this.locationService.findAllVersionsOfLocationById(locationId);
    }

    @Operation(description = "Get specific versions of specific location by its id and by version index",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Return specific version of location when location with specified id is exist and when location version with specified index is exist", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = LocationMemento.class))),
                    @ApiResponse(responseCode = "404", description = "Return when location with specified id is not exist or when versionIndex is incorrect", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "400", description = "Return when request params 'locationId' or 'versionIndex' are not specified or it is null", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class))),
            })
    @GetMapping("/{locationId:\\d+}/versions/{versionIndex:\\d+}")
    public LocationMemento findVersionOfLocationByIndex(@PathVariable Long locationId,
                                                        @PathVariable Integer versionIndex) {
        return this.locationService.findVersionOfLocationByIndex(locationId, versionIndex);
    }

    @Operation(description = "Restores the specific state of location to the current location with specified id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Return restored location", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Location.class))),
                    @ApiResponse(responseCode = "404", description = "Return when location with specified id is not exist or when versionIndex is incorrect", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "400", description = "Return when request params 'locationId' or 'versionIndex' are not specified or it is null", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class))),
            })
    @PostMapping("/{locationId:\\d+}/restore/{versionIndex:\\d+}")
    public Location restoreVersionOfLocation(@PathVariable Long locationId,
                                             @PathVariable Integer versionIndex) {
        return this.locationService.restoreVersionOfLocation(locationId, versionIndex);
    }

}
