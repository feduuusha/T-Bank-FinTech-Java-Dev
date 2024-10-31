package org.tbank.fintech.util;

import org.springframework.stereotype.Component;
import org.tbank.fintech.entity.memento.LocationMemento;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LocationsCaretaker implements Caretaker<Long, LocationMemento> {

    private final ConcurrentHashMap<Long, List<LocationMemento>> map;

    public LocationsCaretaker() {
        this.map = new ConcurrentHashMap<>();
    }

    @Override
    public List<LocationMemento> get(Long id) {
        return map.get(id);
    }

    @Override
    public List<LocationMemento> put(Long id, List<LocationMemento> value) {
        return map.put(id, value);
    }
}
