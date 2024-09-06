package org.tbank.fintech.lesson3;

import java.util.Collection;

public interface CustomLinkedList<E> {
    boolean add(E element);
    E get(int index);
    E remove(int index);
    boolean contains(Object object);
    boolean addAll(Collection<? extends E> c);
    int size();
}
