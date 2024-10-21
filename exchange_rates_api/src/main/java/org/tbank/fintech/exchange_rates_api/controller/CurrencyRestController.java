package org.tbank.fintech.exchange_rates_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.tbank.fintech.exchange_rates_api.controller.payload.ConvertPayload;
import org.tbank.fintech.exchange_rates_api.exception.BadRequestException;
import org.tbank.fintech.exchange_rates_api.model.Event;
import org.tbank.fintech.exchange_rates_api.model.response.Conversion;
import org.tbank.fintech.exchange_rates_api.model.response.CurrencyRate;
import org.tbank.fintech.exchange_rates_api.model.response.exception.ExceptionMessage;
import org.tbank.fintech.exchange_rates_api.service.CurrencyService;

import java.util.concurrent.TimeUnit;

@Tag(name = "Currency controller", description = "Controller for interacting with currencies")
@RestController
@RequestMapping("api/v1/currencies")
@RequiredArgsConstructor
public class CurrencyRestController {

    private final CurrencyService currencyService;

    @Operation(
            summary = "Getting the current exchange rate",
            description = "Getting the current exchange rate of the currency specified in the path variable",
            responses = {
                    @ApiResponse(responseCode = "200", headers = @Header(name = "Cache-Control", description = "max-age=3600"),
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Event.class))),
                    @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionMessage.class)), description = "Incorrect currency code"),
                    @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionMessage.class)), description = "Currency code is not included in the current currency quotes"),
                    @ApiResponse(responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionMessage.class)), description = "Server error"),
                    @ApiResponse(responseCode = "503", headers = @Header(name = "Retry-After", description = "3600"),  content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionMessage.class)), description = "External service is unavailable"),
            }
    )
    @GetMapping(path = "/rates/{code}")
    public ResponseEntity<CurrencyRate> findRatesOfCode(@PathVariable
                                                             @Parameter(required = true, description = "ISO Code of currency", example = "EUR")
                                                             String code) {
        if (code == null || code.isBlank()) {
            throw new BadRequestException("currency code cannot be blank");
        } else {
            return ResponseEntity
                    .ok()
                    .headers((headers) -> headers.setCacheControl(CacheControl.maxAge(1, TimeUnit.HOURS)))
                    .headers((headers) -> headers.setContentType(MediaType.APPLICATION_JSON))
                    .body(this.currencyService.findRatesOfCode(code));
        }
    }

    @Operation(
            summary = "Conversion of one currency into another",
            description = "Request passes two currency codes and the amount of the first one, returns the amount of the second currency",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Conversion.class))),
                    @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionMessage.class)), description = "Incorrect payload"),
                    @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionMessage.class)), description = "Currency code is not included in the current currency quotes"),
                    @ApiResponse(responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionMessage.class)), description = "Server error"),
                    @ApiResponse(responseCode = "503", headers = @Header(name = "Retry-After", description = "3600"), content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionMessage.class)), description = "External service is unavailable"),
            }
    )
    @PostMapping(path = "/convert")
    public ResponseEntity<Conversion> convertCurrency(@Valid @RequestBody ConvertPayload payload,
                                                      BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException(String.join(", ",
                    bindingResult
                            .getAllErrors()
                            .stream()
                            .map(DefaultMessageSourceResolvable::getDefaultMessage)
                            .toList()));
        } else {
            return ResponseEntity
                    .ok()
                    .headers((headers) -> headers.setContentType(MediaType.APPLICATION_JSON))
                    .body(this.currencyService.convertCurrency(payload.fromCurrency(), payload.toCurrency(), payload.amount()));
        }
    }
}
