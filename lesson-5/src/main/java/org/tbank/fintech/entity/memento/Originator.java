package org.tbank.fintech.entity.memento;

public interface Originator<M extends Memento> {
    M createMemento();
    void restore(M memento);

}
