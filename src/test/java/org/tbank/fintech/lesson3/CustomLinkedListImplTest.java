package org.tbank.fintech.lesson3;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

public class CustomLinkedListImplTest {

    @Test
    void addFirstItemTest() {
        // given
        CustomLinkedList<Integer> list = new CustomLinkedListImpl<>();

        // when
        boolean result = list.add(123);

        // then
        Assertions.assertEquals(1, list.size());
        Assertions.assertEquals(123, list.get(0));
        Assertions.assertTrue(result);
    }

    @Test
    void addNotFirstItemTest() {
        // given
        CustomLinkedList<String> list = new CustomLinkedListImpl<>();
        list.add("Thanks");
        list.add("for");

        // when
        boolean result = list.add("T-Bank");

        // then
        Assertions.assertEquals(3, list.size());
        Assertions.assertEquals("T-Bank", list.get(2));
        Assertions.assertTrue(result);
    }

    @Test
    void getFirstItemTest() {
        // given
        CustomLinkedList<String> list = new CustomLinkedListImpl<>();
        list.add("0_0");
        list.add("-_-");
        list.add("*_*");

        // when
        String result = list.get(0);

        // then
        Assertions.assertEquals("0_0", result);
    }

    @Test
    void getLastItemTest() {
        // given
        CustomLinkedList<String> list = new CustomLinkedListImpl<>();
        list.add("0_0");
        list.add("-_-");
        list.add("*_*");

        // when
        String result = list.get(2);

        // then
        Assertions.assertEquals("*_*", result);
    }

    @Test
    void getItemWithIncorrectIndex() {
        // given
        CustomLinkedList<Integer> list = new CustomLinkedListImpl<>();
        list.add(1);
        list.add(2);
        list.add(3);

        // when
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> list.get(3));

    }

    @Test
    void removeFirstItemTest() {
        // given
        CustomLinkedList<String> list = new CustomLinkedListImpl<>();
        list.add("0_0");
        list.add("-_-");
        list.add("*_*");

        // when
        String result = list.remove(0);

        // then
        Assertions.assertEquals("0_0", result);
        Assertions.assertEquals("-_-", list.get(0));
        Assertions.assertEquals(2, list.size());
    }

    @Test
    void removeItemTest() {
        // given
        CustomLinkedList<String> list = new CustomLinkedListImpl<>();
        list.add("0_0");
        list.add("-_-");
        list.add("*_*");

        // when
        String result = list.remove(1);

        // then
        Assertions.assertEquals("-_-", result);
        Assertions.assertEquals("0_0", list.get(0));
        Assertions.assertEquals("*_*", list.get(1));
        Assertions.assertEquals(2, list.size());
    }

    @Test
    void removeLastItemTest() {
        // given
        CustomLinkedList<String> list = new CustomLinkedListImpl<>();
        list.add("0_0");
        list.add("-_-");
        list.add("*_*");

        // when
        String result = list.remove(2);

        // then
        Assertions.assertEquals("*_*", result);
        Assertions.assertEquals("-_-", list.get(1));
        Assertions.assertEquals(2, list.size());
    }

    @Test
    void removeItemWithIncorrectIndex() {
        // given
        CustomLinkedList<Integer> list = new CustomLinkedListImpl<>();
        list.add(1);
        list.add(2);
        list.add(3);

        // when
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> list.remove(3));

    }

    @Test
    void containsNullTest() {
        // given
        CustomLinkedList<Integer> list = new CustomLinkedListImpl<>();
        list.add(5);
        list.add(null);

        // when
        boolean result = list.contains(null);

        // then
        Assertions.assertTrue(result);
    }

    @Test
    void containsItemTest() {
        // given
        CustomLinkedList<Integer> list = new CustomLinkedListImpl<>();
        IntStream.range(0, 100000).forEach(list::add);

        // when
        boolean result = list.contains(2005);

        // then
        Assertions.assertTrue(result);
    }

    @Test
    void notContainsItemTest() {
        // given
        CustomLinkedList<Integer> list = new CustomLinkedListImpl<>();
        IntStream.range(0, 100000).filter((i) -> i % 5 != 0).forEach(list::add);

        // when
        boolean result = list.contains(2005);

        // then
        Assertions.assertFalse(result);
    }

    @Test
    void notContainsItemInEmptyListTest() {
        // given
        CustomLinkedList<Integer> list = new CustomLinkedListImpl<>();

        // when
        boolean result = list.contains(2005);

        // then
        Assertions.assertFalse(result);
    }

    @Test
    void addAllItemsFromOtherCollectionTest() {
        // given
        CustomLinkedList<Number> list = new CustomLinkedListImpl<>();
        List<Integer> onlyOddList = IntStream.range(0, 10_000).filter((i) -> i % 2 != 0).boxed().toList();

        // when
        boolean result = list.addAll(onlyOddList);

        // then
        Assertions.assertEquals(onlyOddList.size(), list.size());
        Assertions.assertTrue(result);
        for (int i = 0; i < list.size(); ++i) {
            Assertions.assertEquals(onlyOddList.get(i), list.get(i));
        }
    }

    @Test
    void addAllItemsFromEmptyCollectionTest() {
        // given
        CustomLinkedList<Number> list = new CustomLinkedListImpl<>();
        List<Integer> onlyOddList = new LinkedList<>();

        // when
        boolean result = list.addAll(onlyOddList);

        // then
        Assertions.assertFalse(result);
    }
}
