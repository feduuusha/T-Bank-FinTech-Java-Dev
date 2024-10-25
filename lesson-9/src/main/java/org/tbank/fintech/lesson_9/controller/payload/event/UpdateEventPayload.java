package org.tbank.fintech.lesson_9.controller.payload.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

public record UpdateEventPayload(
        @NotBlank
        @Length(max = 256)
        String name,
        @NotNull
        @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
        LocalDateTime date,
        @NotNull
        @Positive
        Long placeId,
        String description
) {
}
