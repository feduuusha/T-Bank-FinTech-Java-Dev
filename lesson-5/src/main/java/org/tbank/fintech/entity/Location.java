package org.tbank.fintech.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tbank.fintech.entity.memento.LocationMemento;
import org.tbank.fintech.entity.memento.Originator;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Location implements Originator<LocationMemento> {
    private Long id;
    private String slug;
    private String name;
    private String timezone;
    private Coords coords;
    private String language;

    public Location(String slug, String name, String timezone, Coords coords, String language) {
        this.slug = slug;
        this.name = name;
        this.timezone = timezone;
        this.coords = coords;
        this.language = language;
    }



    @Override
    public LocationMemento createMemento() {
        return new LocationMemento(slug, name, timezone, new Coords(coords.lat, coords.lon), language);
    }

    @Override
    public void restore(LocationMemento memento) {
        slug = memento.slug();
        name = memento.name();
        timezone = memento.timezone();
        coords = memento.coords();
        language = memento.language();
    }
}
