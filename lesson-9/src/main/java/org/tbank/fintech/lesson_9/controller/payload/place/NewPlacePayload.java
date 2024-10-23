package org.tbank.fintech.lesson_9.controller.payload.place;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;

public record NewPlacePayload (
        @NotBlank
        @Length(max = 64)
        String slug,
        @Positive
        @NotNull
        Double lat,
        @Positive
        @NotNull
        Double lon,
        @NotBlank
        @Length(max=256)
        String name,
        @NotBlank
        @Length(max=128)
        String timezone,
        @NotBlank
        @Length(max=64)
        String language
) {
}
