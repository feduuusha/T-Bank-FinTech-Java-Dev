package org.tbank.fintech.lesson_9.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.tbank.fintech.lesson_9.entity.Event;
import org.tbank.fintech.lesson_9.repository.EventRepository;
import org.tbank.fintech.lesson_9.repository.PlaceRepository;
import org.tbank.fintech.lesson_9.service.EventService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final PlaceRepository placeRepository;

    @Override
    public List<Event> findAllEventsByFilter(String eventName, String placeName, LocalDateTime fromDate, LocalDateTime toDate) {
        Specification<Event> eventSpecification = EventRepository.buildFilterSpecification(eventName, placeName, fromDate, toDate);
        return this.eventRepository.findAll(eventSpecification);
    }

    @Override
    public Event createEvent(String name, LocalDateTime date, Long placeId, String description) {
        var place = this.placeRepository.findById(placeId).orElseThrow(() -> new EntityNotFoundException("Place with id: " + placeId + " does not exist"));
        return this.eventRepository.save(new Event(null, name, date, place, description));
    }

    @Override
    public Event findEventById(Long eventId) {
        return this.eventRepository.findById(eventId).orElseThrow(() -> new NoSuchElementException("Event with id: " + eventId + " not found"));
    }

    @Override
    public void updateEventById(Long eventId, String name, LocalDateTime date, Long placeId, String description) {
        var event = this.eventRepository.findById(eventId).orElseThrow(() -> new NoSuchElementException("Event with id: " + eventId + " not found"));
        var place = this.placeRepository.findById(placeId).orElseThrow(() -> new EntityNotFoundException("Place with id: " + placeId + " does not exist"));
        event.setName(name);
        event.setDate(date);
        event.setPlace(place);
        event.setDescription(description);
        this.eventRepository.save(event);
    }

    @Override
    public void deleteEventById(Long eventId) {
        var event = this.eventRepository.findById(eventId).orElseThrow(() -> new NoSuchElementException("Event with id: " + eventId + " not found"));
        this.eventRepository.delete(event);
    }
}
