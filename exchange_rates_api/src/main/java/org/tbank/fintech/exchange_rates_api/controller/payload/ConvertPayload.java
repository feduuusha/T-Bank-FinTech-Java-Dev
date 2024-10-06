package org.tbank.fintech.exchange_rates_api.controller.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Payload for converting a certain amount of one currency into another")
public record ConvertPayload (
        @Schema(example = "EUR")
        @NotBlank(message = "FromCurrency cannot be blank")
        String fromCurrency,
        @Schema(example = "USD")
        @NotBlank(message = "ToCurrency cannot be blank")
        String toCurrency,
        @Schema(example = "69.9")
        @Positive(message = "Amount should be positive")
        @NotNull(message = "Amount cannot be null")
        Double amount
) {

}
