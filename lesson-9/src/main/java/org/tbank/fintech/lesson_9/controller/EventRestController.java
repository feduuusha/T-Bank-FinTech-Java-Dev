package org.tbank.fintech.lesson_9.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import org.tbank.fintech.lesson_9.controller.payload.event.NewEventPayload;
import org.tbank.fintech.lesson_9.controller.payload.event.UpdateEventPayload;
import org.tbank.fintech.lesson_9.entity.Event;
import org.tbank.fintech.lesson_9.exception.ExceptionMessage;
import org.tbank.fintech.lesson_9.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Events controller", description = "CRUD operation with events")
@RestController
@RequestMapping("api/v1/events")
@RequiredArgsConstructor
public class EventRestController {

    private final EventService eventService;

    @Operation(description = "Find events by name (event name) or by place (place name) or by fromDate and toDate (it only works when both parameters are passed). if the parameter is not passed, then this field can be any.",
    responses = {
            @ApiResponse(responseCode = "200", description = "Correct response list of Event objects" ,content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Event.class))),
            @ApiResponse(responseCode = "400", description = "Returning when request params is incorrect",content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionMessage.class)))})
    @PreAuthorize("hasAuthority('read_event')")
    @GetMapping
    public List<Event> findAllEventsByFilter(@RequestParam(name = "name", required = false) String eventName,
                                             @RequestParam(name = "place", required = false) String placeName,
                                             @RequestParam(name = "fromDate", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm") LocalDateTime fromDate,
                                             @RequestParam(name = "toDate", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm") LocalDateTime toDate) {
        return this.eventService.findAllEventsByFilter(eventName, placeName, fromDate, toDate);
    }

    @Operation(description = "Create Event and save it in database",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Correct response created Event object" ,content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Event.class))),
                    @ApiResponse(responseCode = "400", description = "Returning when request params is incorrect or request body is incorrect",content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionMessage.class)))})
    @PreAuthorize("hasAuthority('create_event')")
    @PostMapping
    public ResponseEntity<?> createEvent(@Valid @RequestBody NewEventPayload payload,
                                      BindingResult bindingResult,
                                      UriComponentsBuilder uriComponentsBuilder) throws BindException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        } else {
            Event event = this.eventService.createEvent(payload.name(), payload.date(), payload.placeId(), payload.description());
            return ResponseEntity
                    .created(uriComponentsBuilder
                        .replacePath("/api/v1/events/{eventId}")
                        .build(event.getId()))
                    .body(event);
        }
    }

    @Operation(description = "Find event by id.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Correct response Event objects" ,content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Event.class))),
                    @ApiResponse(responseCode = "404", description = "Returning when event with specified id is not exist",content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionMessage.class)))})
    @PreAuthorize("hasAuthority('read_event')")
    @GetMapping("/{eventId:\\d+}")
    public Event findEventById(@PathVariable Long eventId) {
        return this.eventService.findEventById(eventId);
    }


    @Operation(description = "Update Event with specified id and save it in database",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Correct response, object is updated"),
                    @ApiResponse(responseCode = "400", description = "Returning when request params is incorrect or request body is incorrect",content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionMessage.class))),
                    @ApiResponse(responseCode = "404", description = "Returning when event with specified id is not exist",content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionMessage.class)))})
    @PreAuthorize("hasAuthority('update_event')")
    @PutMapping("/{eventId:\\d+}")
    public ResponseEntity<Void> updateEventById(@PathVariable Long eventId,
                                                  @Valid @RequestBody UpdateEventPayload payload,
                                                  BindingResult bindingResult) throws BindException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        } else {
            this.eventService.updateEventById(eventId, payload.name(), payload.date(), payload.placeId(), payload.description());
            return ResponseEntity.noContent().build();
        }
    }

    @Operation(description = "Delete events by id.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Correct response object is deleted"),
                    @ApiResponse(responseCode = "404", description = "Returning when event with specified id is not exist",content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionMessage.class)))})
    @PreAuthorize("hasAuthority('remove_event')")
    @DeleteMapping("/{eventId:\\d+}")
    public ResponseEntity<Void> deleteEventById(@PathVariable Long eventId) {
        this.eventService.deleteEventById(eventId);
        return ResponseEntity.noContent().build();
    }

}
