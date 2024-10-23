package org.tbank.fintech.lesson_9.exception;

import io.swagger.v3.oas.annotations.media.Schema;

import java.sql.Timestamp;

@Schema(description = "Body of the error response containing the error code and its message")
public record ExceptionMessage(
        Timestamp timestamp,
        @Schema(example = "400")
        Integer status,
        @Schema(example = "Bad Request")
        String error,
        @Schema(example = "/api/v1/events/-5")
        String path
) {
}
