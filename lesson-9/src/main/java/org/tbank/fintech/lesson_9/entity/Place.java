package org.tbank.fintech.lesson_9.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;
import org.tbank.fintech.lesson_9.listener.LoggingEntityListener;
import org.tbank.fintech.lesson_9.listener.PlaceValidationEntityListener;
import org.tbank.fintech.lesson_9.listener.StatisticEntityListener;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "places")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners({LoggingEntityListener.class, StatisticEntityListener.class, PlaceValidationEntityListener.class})
@Schema(description = "Object representing the place")
public class Place {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "places_seq")
    @SequenceGenerator(name = "places_seq", sequenceName = "places_seq", allocationSize = 1)
    private Long id;
    private String slug;
    private Double lat;
    private Double lon;
    private String name;
    private String timezone;
    private String language;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "place", cascade = {CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE})
    @JsonIgnore
    private List<Event> events;

    @Override
    public final boolean equals(Object object) {
        if (this == object) return true;
        if (object == null) return false;
        Class<?> oEffectiveClass = object instanceof HibernateProxy ? ((HibernateProxy) object).getHibernateLazyInitializer().getPersistentClass() : object.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Place place = (Place) object;
        return getId() != null && Objects.equals(getId(), place.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
