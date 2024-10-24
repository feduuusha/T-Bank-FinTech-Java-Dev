package org.tbank.fintech.lesson_9.repository;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.tbank.fintech.lesson_9.entity.Event;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    static Specification<Event> buildFilterSpecification(String name, String placeName, LocalDateTime dateFrom, LocalDateTime dateTo) {
        return (root, query, criteriaBuilder) -> {
            root.fetch("place");
            List<Predicate> predicates = new LinkedList<>();
            if (name != null) {
                predicates.add(criteriaBuilder.equal(root.get("name"), name));
            }
            if (placeName != null) {
                predicates.add(criteriaBuilder.equal(root.get("place").get("name"), placeName));
            }
            if (dateFrom != null && dateTo != null) {
                predicates.add(criteriaBuilder.between(root.get("date"), dateFrom, dateTo));
            }
            return predicates.stream().reduce(criteriaBuilder::and).orElse(null);
        };
    }
}
