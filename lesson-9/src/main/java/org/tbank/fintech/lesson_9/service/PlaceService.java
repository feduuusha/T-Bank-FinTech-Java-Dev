package org.tbank.fintech.lesson_9.service;

import org.tbank.fintech.lesson_9.entity.Place;

import java.util.List;

public interface PlaceService {
    List<Place> findAllPlaces();

    Place createPlace(String slug, Double lat, Double lon, String name, String timezone, String language);

    Place findPlaceById(Long placeId);

    void updatePlaceById(Long placeId, String slug, Double lat, Double lon, String name, String timezone, String language);

    void deletePlaceById(Long placeId);
}
