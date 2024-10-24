package org.tbank.fintech.lesson_9.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.tbank.fintech.lesson_9.entity.Place;

import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Long> {
    @Query("SELECT p FROM Place p LEFT JOIN FETCH p.events e WHERE p.id = :placeId")
    Optional<Place> findByIdWithEvents(@Param("placeId") Long placeId);
}
