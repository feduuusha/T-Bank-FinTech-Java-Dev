package org.tbank.fintech.lesson_9.listener;

import jakarta.persistence.*;
import jakarta.validation.ValidationException;
import org.tbank.fintech.lesson_9.entity.Place;

public class PlaceValidationEntityListener {

    private static void validatePlace(Place o) {
        if (o.getName() == null || o.getName().isBlank() || o.getName().length() > 256) throw new ValidationException("Place name can not be null or blank or have length > 256");
        if (o.getSlug() == null || o.getSlug().isBlank() || o.getSlug().length() > 64) throw new ValidationException("Place slug can not be null or blank or have length > 64");
        if (o.getTimezone() == null || o.getTimezone().isBlank() || o.getTimezone().length() > 128) throw new ValidationException("Place timezone can not be null or blank or have length > 128");
        if (o.getLanguage() == null || o.getLanguage().isBlank() || o.getLanguage().length() > 64) throw new ValidationException("Place language can not be null or blank or have length > 64");
        if (o.getLat() == null || o.getLat() < 0) throw new ValidationException("Place lat can not be null or non-positive");
        if (o.getLon() == null || o.getLon() < 0) throw new ValidationException("Place lon can not be null or non-positive");
    }

    @PrePersist
    public void prePersist(Place o) {
        validatePlace(o);
    }

    @PreUpdate
    public void preUpdate(Place o) {
        validatePlace(o);
    }

}
