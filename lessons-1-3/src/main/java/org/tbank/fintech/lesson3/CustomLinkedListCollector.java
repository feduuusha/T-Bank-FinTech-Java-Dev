package org.tbank.fintech.lesson3;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class CustomLinkedListCollector<T> implements Collector<T, CustomLinkedList<T>, CustomLinkedList<T>> {
    @Override
    public Supplier<CustomLinkedList<T>> supplier() {
        return CustomLinkedListImpl::new;
    }

    @Override
    public BiConsumer<CustomLinkedList<T>, T> accumulator() {
        return CustomLinkedList::add;
    }

    @Override
    public BinaryOperator<CustomLinkedList<T>> combiner() {
        return (list1, list2) -> {
            list1.addAll(list2);
            return list1;
        };
    }

    @Override
    public Function<CustomLinkedList<T>, CustomLinkedList<T>> finisher() {
        return Function.identity();
    }

    @Override
    public Set<Characteristics> characteristics() {
        return EnumSet.of(Characteristics.IDENTITY_FINISH);
    }
}
