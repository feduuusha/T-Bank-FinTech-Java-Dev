package org.tbank.fintech.lesson_9.controller.payload.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ResetPasswordPayload (
        @Email
        @NotBlank
        String email
) {
}
