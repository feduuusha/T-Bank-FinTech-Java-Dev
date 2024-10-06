package org.tbank.fintech.exchange_rates_api.model.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Object containing the source currency, destination currency, and the amount transferred.")
public record Conversion(
        @Schema(example = "USD")
        String fromCurrency,
        @Schema(example = "RUB")
        String toCurrency,
        @Schema(example = "599.99")
        Double convertedAmount
) {
}
