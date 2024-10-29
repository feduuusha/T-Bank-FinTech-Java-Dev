package org.tbank.fintech.util;

import org.tbank.fintech.entity.memento.Memento;

import java.util.List;

public interface Caretaker<ID, M extends Memento> {
    List<M> get(ID id);
    List<M> put(ID id, List<M> value);
}
