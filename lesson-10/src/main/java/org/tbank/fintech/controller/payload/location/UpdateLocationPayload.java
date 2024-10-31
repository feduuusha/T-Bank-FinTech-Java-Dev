package org.tbank.fintech.controller.payload.location;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.tbank.fintech.entity.Coords;

public record UpdateLocationPayload (
        @NotBlank
        String slug,
        @NotBlank
        String name,
        @NotBlank
        String timezone,
        @NotNull
        Coords coords,
        @NotBlank
        String language
) {
}
