package org.tbank.fintech.exchange_rates_api.model.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Object containing a currency and its exchange rate to the RUB")
public record CurrencyRate(
        @Schema(example = "USD")
        String currency,
        @Schema(example = "90.78")
        Double rate
) {
}
