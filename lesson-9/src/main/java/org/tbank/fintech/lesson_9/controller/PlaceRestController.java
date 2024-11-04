package org.tbank.fintech.lesson_9.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import org.tbank.fintech.lesson_9.controller.payload.place.NewPlacePayload;
import org.tbank.fintech.lesson_9.controller.payload.place.UpdatePlacePayload;
import org.tbank.fintech.lesson_9.entity.Place;
import org.tbank.fintech.lesson_9.exception.ExceptionMessage;
import org.tbank.fintech.lesson_9.service.PlaceService;

import java.util.List;

@Tag(name = "Places controller", description = "CRUD operations with places")
@RestController
@RequestMapping("api/v1/places")
@RequiredArgsConstructor
public class PlaceRestController {

    private final PlaceService placeService;

    @Operation(description = "Find all places.",
            responses = @ApiResponse(responseCode = "200", description = "Correct response list of Event objects" ,content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Place.class))))
    @PreAuthorize("hasAuthority('read_place')")
    @GetMapping
    public List<Place> findAllPlaces() {
        return this.placeService.findAllPlaces();
    }

    @Operation(description = "Create Place and save it in database",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Correct response created Place object" ,content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Place.class))),
                    @ApiResponse(responseCode = "400", description = "Returning when request params is incorrect or request body is incorrect",content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionMessage.class)))})
    @PreAuthorize("hasAuthority('create_place')")
    @PostMapping
    public ResponseEntity<?> createPlace(@Valid @RequestBody NewPlacePayload payload,
                                      BindingResult bindingResult,
                                      UriComponentsBuilder uriComponentsBuilder) throws BindException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        } else {
            Place place = this.placeService.createPlace(payload.slug(), payload.lat(), payload.lon(), payload.name(), payload.timezone(), payload.language());
            return ResponseEntity
                    .created(uriComponentsBuilder
                        .replacePath("/api/v1/places/{placeId}")
                        .build(place.getId()))
                    .body(place);
        }

    }

    @Operation(description = "Find place by id.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Correct response Place objects" ,content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Place.class))),
                    @ApiResponse(responseCode = "404", description = "Returning when place with specified id is not exist",content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionMessage.class)))})
    @PreAuthorize("hasAuthority('read_place')")
    @GetMapping("/{placeId:\\d+}")
    public Place findPlaceById(@PathVariable Long placeId) {
        return this.placeService.findPlaceById(placeId);
    }

    @Operation(description = "Update Place with specified id and save it in database",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Correct response, object is updated"),
                    @ApiResponse(responseCode = "400", description = "Returning when request params is incorrect or request body is incorrect",content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionMessage.class))),
                    @ApiResponse(responseCode = "404", description = "Returning when place with specified id is not exist",content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionMessage.class)))})
    @PreAuthorize("hasAuthority('update_place')")
    @PutMapping("/{placeId:\\d+}")
    public ResponseEntity<Void> updatePlaceById(@PathVariable Long placeId,
                                                @Valid @RequestBody UpdatePlacePayload payload,
                                                BindingResult bindingResult) throws BindException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        } else {
            this.placeService.updatePlaceById(placeId, payload.slug(), payload.lat(), payload.lon(), payload.name(), payload.timezone(), payload.language());
            return ResponseEntity.noContent().build();
        }
    }

    @Operation(description = "Delete place by id.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Correct response object is deleted"),
                    @ApiResponse(responseCode = "404", description = "Returning when place with specified id is not exist",content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionMessage.class)))})
    @PreAuthorize("hasAuthority('remove_place')")
    @DeleteMapping("/{placeId:\\d+}")
    public ResponseEntity<Void> deletePlaceById(@PathVariable Long placeId) {
        this.placeService.deletePlaceById(placeId);
        return ResponseEntity.noContent().build();
    }
}
