package org.tbank.fintech.lesson_9.service;

import org.tbank.fintech.lesson_9.entity.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    List<Event> findAllEventsByFilter(String eventName, String placeName, LocalDateTime fromDate, LocalDateTime toDate);

    Event createEvent(String name, LocalDateTime date, Long placeId, String description);

    Event findEventById(Long eventId);

    void updateEventById(Long eventId, String name, LocalDateTime date, Long placeId, String description);

    void deleteEventById(Long eventId);
}
