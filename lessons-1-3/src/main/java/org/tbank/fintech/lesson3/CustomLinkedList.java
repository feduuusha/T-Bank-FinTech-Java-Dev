package org.tbank.fintech.lesson3;

import java.util.Collection;

public interface CustomLinkedList<E> extends Iterable<E>{
    boolean add(E element);
    E get(int index);
    E remove(int index);
    boolean contains(Object object);
    boolean addAll(Collection<? extends E> c);
    boolean addAll(CustomLinkedList<? extends E> c);
    int size();
    @SafeVarargs
    static <E> CustomLinkedList<E> of(E... elements) {
        CustomLinkedList<E> list = new CustomLinkedListImpl<>();
        for (E element : elements) {
            list.add(element);
        }
        return list;
    }
}
