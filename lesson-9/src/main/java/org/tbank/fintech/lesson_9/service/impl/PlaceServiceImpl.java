package org.tbank.fintech.lesson_9.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.tbank.fintech.lesson_9.entity.Place;
import org.tbank.fintech.lesson_9.repository.PlaceRepository;
import org.tbank.fintech.lesson_9.service.PlaceService;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class PlaceServiceImpl implements PlaceService {

    private final PlaceRepository placeRepository;

    @Override
    public List<Place> findAllPlaces() {
        return this.placeRepository.findAll();
    }

    @Override
    public Place createPlace(String slug, Double lat, Double lon, String name, String timezone, String language) {
        return this.placeRepository.save(new Place(null, slug, lat, lon, name, timezone, language, null));
    }

    @Override
    public Place findPlaceById(Long placeId) {
        return this.placeRepository.findByIdWithEvents(placeId).orElseThrow(() -> new NoSuchElementException("Place with id: " + placeId + " not found"));
    }

    @Override
    public void updatePlaceById(Long placeId, String slug, Double lat, Double lon, String name, String timezone, String language) {
        var place = this.placeRepository.findById(placeId).orElseThrow(() -> new NoSuchElementException("Place with id: " + placeId + " not found"));
        place.setSlug(slug);
        place.setLat(lat);
        place.setLon(lon);
        place.setName(name);
        place.setTimezone(timezone);
        place.setLanguage(language);
        this.placeRepository.save(place);
    }

    @Override
    public void deletePlaceById(Long placeId) {
        var place = this.placeRepository.findById(placeId).orElseThrow(() -> new NoSuchElementException("Place with id: " + placeId + " not found"));
        this.placeRepository.delete(place);
    }
}
