package org.tbank.fintech.lesson3;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class CustomIteratorTest {

    @Test
    @DisplayName("hasNext() and when next() should work list.size() times")
    public void hasNextTest() {
        // Arrange
        CustomLinkedList<Integer> ints = IntStream.range(1, 1000).boxed().collect(new CustomLinkedListCollector<>());
        Iterator<Integer> iterator = ints.iterator();

        // Act
        int counter = 0;
        while (iterator.hasNext()) {
            iterator.next();
            ++counter;
        }

        // Assert
        assertThat(counter).isEqualTo(ints.size());
    }

    @Test
    @DisplayName("next() when all elements in list already iterated should throw NoSuchElementException()")
    public void nextTest() {
        // Arrange
        CustomLinkedList<Integer> ints = IntStream.range(1, 1000).boxed().collect(new CustomLinkedListCollector<>());
        Iterator<Integer> iterator = ints.iterator();
        while (iterator.hasNext()) {
            iterator.next();
        }

        // Act
        // Assert
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(iterator::next).withMessage("All elements already iterated");
    }

    @Test
    @DisplayName("when in collection add or remove element when next call of next() should throw ConcurrentModificationException()")
    public void checkForModificationTest() {
        // Arrange
        CustomLinkedList<Integer> list = CustomLinkedList.of(1, 2, 3);
        var iterator = list.iterator();

        // Act
        iterator.next();
        list.add(4);
        // Assert
        assertThatExceptionOfType(ConcurrentModificationException.class)
                .isThrownBy(iterator::next).withMessage("Do not change list when iterating over it");
    }

    @Test
    @DisplayName("forEachRemaining() should accept action for all items in list")
    public void forEachRemainingTest() {
        // Arrange
        CustomLinkedList<Integer> list = CustomLinkedList.of(1, 2, 3);
        var iterator = list.iterator();
        List<Integer> containerForInts = new ArrayList<>();

        // Act
        iterator.forEachRemaining(containerForInts::add);

        // Assert
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(containerForInts.size()).isEqualTo(list.size());
        for (int i = 0; i < containerForInts.size(); ++i) {
            softly.assertThat(list.get(i)).isEqualTo(containerForInts.get(i));
        }

        softly.assertAll();
    }
}
