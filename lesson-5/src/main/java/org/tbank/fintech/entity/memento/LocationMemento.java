package org.tbank.fintech.entity.memento;

import org.tbank.fintech.entity.Coords;

public record LocationMemento (
        String slug,
        String name,
        String timezone,
        Coords coords,
        String language
) implements Memento {
}
