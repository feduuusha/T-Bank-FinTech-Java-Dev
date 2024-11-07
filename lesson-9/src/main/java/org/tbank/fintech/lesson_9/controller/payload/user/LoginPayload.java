package org.tbank.fintech.lesson_9.controller.payload.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginPayload(
        @NotBlank
        String username,
        @NotBlank
        String password,
        @NotNull
        Boolean rememberMe
) {
}
