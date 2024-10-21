package org.tbank.fintech.exchange_rates_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.tbank.fintech.exchange_rates_api.model.Event;
import org.tbank.fintech.exchange_rates_api.model.response.exception.ExceptionMessage;
import org.tbank.fintech.exchange_rates_api.service.EventService;

import java.util.Date;
import java.util.List;

@Tag(name = "Events controller", description = "Controller for interacting with events")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/events")
public class EventRestController {

    private final EventService eventService;

    @Operation(
            summary = "A list of the most popular events from dateFrom to dateTo that can be attended with the specified budget in the specified currency.",
            description = "If at least one of the two dates is not specified, then the most popular events that meet our conditions for the current week will be found and the price tag for the event may differ from the real one",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Event.class))),
                    @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionMessage.class)), description = "Incorrect currency code or other bad requests"),
                    @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionMessage.class)), description = "Currency code is not included in the current currency quotes or not valid url"),
                    @ApiResponse(responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionMessage.class)), description = "Server error"),
                    @ApiResponse(responseCode = "503", headers = @Header(name = "Retry-After", description = "3600"),  content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionMessage.class)), description = "External service is unavailable"),
            }
    )
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Event> findPopularEventsFromPeriod(@Parameter(example = "300") @RequestParam int budget,
                                                   @Parameter(example = "RUB") @RequestParam String currency,
                                                   @Parameter(example = "15-09-2002", schema = @Schema(format = "dd-MM-yyyy")) @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date dateFrom,
                                                   @Parameter(example = "17-10-2003", schema = @Schema(format = "dd-MM-yyyy")) @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date dateTo,
                                                                      @RequestParam(required = false, defaultValue = "msk") String location,
                                                                      @RequestParam(required = false, defaultValue = "1") int page,
                                                                      @RequestParam(required = false, defaultValue = "20") int page_size) {
        return this.eventService.findPopularEventsFromPeriod(budget, currency, dateFrom, dateTo, location, page, page_size).join();
    }
}
