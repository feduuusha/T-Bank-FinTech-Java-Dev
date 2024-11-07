package org.tbank.fintech.lesson_9.controller.payload.user;

import jakarta.validation.constraints.*;

public record NewUserPayload (
        @Email
        @NotBlank
        String email,
        @NotBlank
        String firstName,
        @NotBlank
        String lastName,
        @NotBlank
        @Pattern(regexp = "^(?=.*[a-zA-Z])[a-zA-Z0-9]+$")
        @Size(min = 3)
        String username,
        @Size(min = 8)
        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9]+$")
        @NotBlank
        String password
) {
}
