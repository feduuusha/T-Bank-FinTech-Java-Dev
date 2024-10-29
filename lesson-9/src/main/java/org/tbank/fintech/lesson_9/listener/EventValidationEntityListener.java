package org.tbank.fintech.lesson_9.listener;

import jakarta.persistence.*;
import jakarta.validation.ValidationException;
import org.tbank.fintech.lesson_9.entity.Event;

import java.time.LocalDateTime;

public class EventValidationEntityListener {

    private static void validateEvent(Event o) {
        if (o.getName() == null || o.getName().isBlank()) throw new ValidationException("Event name can not be null or blank");
        if (o.getDate() == null || o.getDate().isBefore(LocalDateTime.of(1900, 1, 1, 0, 0))) throw new ValidationException("Event date can not be null or before 01-01-1900 00:00");
        if (o.getDescription() != null && o.getDescription().isBlank()) throw new ValidationException("Event description can not be blank if it exist");
    }

    @PrePersist
    public void prePersist(Event o) {
        validateEvent(o);
    }

    @PreUpdate
    public void preUpdate(Event o) {
        validateEvent(o);
    }
}
