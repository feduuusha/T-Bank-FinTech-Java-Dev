package org.tbank.fintech.exchange_rates_api.model.response.exception;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Body of the error response containing the error code and its message")
public record ExceptionMessage(
        @Schema(example = "400")
        Integer code,
        @Schema(example = "Bad Request")
        String message
) {
}
