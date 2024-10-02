package org.tbank.fintech.lesson3;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;

public class CustomLinkedListImplTest {

    @Test
    @DisplayName("Checking the correctness of adding the first element in list")
    void addFirstItemTest() {
        // given
        CustomLinkedList<Integer> list = new CustomLinkedListImpl<>();

        // when
        boolean result = list.add(123);

        // then
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(list.size()).isEqualTo(1).as(() -> "one element was added, but list size is " + list.size());
        softly.assertThat(list.get(0)).isEqualTo(123).as(() -> "only 123 was added, but element at 0 index is " + list.get(0));
        softly.assertThat(result).isTrue().as(() -> "element was added, but method return " + result);

        softly.assertAll();
    }

    @Test
    @DisplayName("Checking the correctness of adding an element to the end of list")
    void addNotFirstItemTest() {
        // given
        CustomLinkedList<String> list = CustomLinkedList.of("Thanks", "for");

        // when
        boolean result = list.add("T-Bank");

        // then
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(list.size()).isEqualTo(3).as(() -> "three elements was added, but list size is " + list.size());
        softly.assertThat(list.get(2)).isEqualTo("T-Bank").as(() -> "\"T-Bank\" was added at index 2, but element at index 2 is " + list.get(2));
        softly.assertThat(result).isTrue().as(() -> "element was added, but method return " + result);

        softly.assertAll();
    }

    @Test
    @DisplayName("Checking the correctness of getting a first element of list")
    void getFirstItemTest() {
        // given
        CustomLinkedList<String> list = new CustomLinkedListImpl<>();
        list.add("0_0");
        list.add("-_-");
        list.add("*_*");

        // when
        String result = list.get(0);

        // then
        assertThat(result).isEqualTo("0_0").as(() -> "at 0 index was added \"0_0\", but list.get(0) result is " + result);
    }

    @Test
    @DisplayName("Checking the correctness of getting a last element of list")
    void getLastItemTest() {
        // given
        CustomLinkedList<String> list = new CustomLinkedListImpl<>();
        list.add("0_0");
        list.add("-_-");
        list.add("*_*");

        // when
        String result = list.get(2);

        // then
        assertThat(result).isEqualTo("*_*").as(() -> "at 2 index was added \"*_*\", but list.get(2) result is " + result);
    }

    @Test
    @DisplayName("Checking that getting a element with incorrect index will throw IndexOutOfBoundsException")
    void getItemWithIncorrectIndex() {
        // given
        CustomLinkedList<Integer> list = new CustomLinkedListImpl<>();
        list.add(1);
        list.add(2);
        list.add(3);

        // when
        assertThatIndexOutOfBoundsException().isThrownBy(() -> list.get(3)).as(() -> "3 items were added, but list.get(3) did not throw an IndexOutOfBoundsException");
    }

    @Test
    @DisplayName("Checking the correctness of removing a first element of list")
    void removeFirstItemTest() {
        // given
        CustomLinkedList<String> list = new CustomLinkedListImpl<>();
        list.add("0_0");
        list.add("-_-");
        list.add("*_*");

        // when
        String result = list.remove(0);

        // then
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(result).isEqualTo("0_0").as(() -> "\"0_0\" was added at index 0, but list.remove(0) return " + result);
        softly.assertThat(list.get(0)).isEqualTo("-_-").as(() -> "\"-_-\" was added at 1 index, but after list.remove(0) list.get(0) return " + list.get(0));
        softly.assertThat(list.size()).isEqualTo(2).as(() -> "three elements was added and one removing, but list size is " + list.size());

        softly.assertAll();
    }

    @Test
    @DisplayName("Checking the correctness of removing a element of list")
    void removeItemTest() {
        // given
        CustomLinkedList<String> list = new CustomLinkedListImpl<>();
        list.add("0_0");
        list.add("-_-");
        list.add("*_*");

        // when
        String result = list.remove(1);

        // then
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(result).isEqualTo("-_-").as(() -> "\"-_-\" was added at index 1, but list.remove(1) return " + result);
        softly.assertThat(list.get(0)).isEqualTo("0_0").as(() -> "\"0_0\" was added at index 0, but list.get(0) return " + list.get(0));
        softly.assertThat(list.get(1)).isEqualTo("*_*").as(() -> "\"*_*\" was added at index 2 and after list.remove(0) it index must be 1, but list.get(1) return " + list.get(1));

        softly.assertAll();
    }

    @Test
    @DisplayName("Checking the correctness of removing a last element of list")
    void removeLastItemTest() {
        // given
        CustomLinkedList<String> list = new CustomLinkedListImpl<>();
        list.add("0_0");
        list.add("-_-");
        list.add("*_*");

        // when
        String result = list.remove(2);

        // then
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(result).isEqualTo("*_*").as(() -> "\"*_*\" was added at index 2, but list.remove(2) return " + result);
        softly.assertThat(list.get(1)).isEqualTo("-_-").as(() ->  "\"-_-\" was added at index 1, but list.get(1) return " + list.get(1));
        softly.assertThat(list.size()).isEqualTo(2).as(() -> "was added 3 elements and one removing, but list.size() return " + list.size());

        softly.assertAll();
    }

    @Test
    @DisplayName("Checking that removing a element with incorrect index will throw IndexOutOfBoundsException")
    void removeItemWithIncorrectIndex() {
        // given
        CustomLinkedList<Integer> list = new CustomLinkedListImpl<>();
        list.add(1);
        list.add(2);
        list.add(3);

        // when
        assertThatIndexOutOfBoundsException().isThrownBy(() -> list.remove(3)).as(() -> "3 items were added, but list.remove(3) did not throw an IndexOutOfBoundsException");
    }

    @Test
    @DisplayName("Checking the correctness of the contains method with the null element")
    void containsNullTest() {
        // given
        CustomLinkedList<Integer> list = new CustomLinkedListImpl<>();
        list.add(5);
        list.add(null);

        // when
        boolean result = list.contains(null);

        // then
        assertThat(result).isTrue().as(() -> "was added null, but list.contains(null) return false");
    }

    @Test
    @DisplayName("Checking the correctness of the contains method with the element")
    void containsItemTest() {
        // given
        CustomLinkedList<Integer> list = new CustomLinkedListImpl<>();
        IntStream.range(0, 100000).forEach(list::add);

        // when
        boolean result = list.contains(2005);

        // then
        assertThat(result).isTrue().as(() -> "was added 2005, but list.contains(2005) return false");
    }

    @Test
    @DisplayName("Checking the correctness of the contains method with an element that doesn't exist")
    void notContainsItemTest() {
        // given
        CustomLinkedList<Integer> list = new CustomLinkedListImpl<>();
        IntStream.range(0, 100000).filter((i) -> i % 5 != 0).forEach(list::add);

        // when
        boolean result = list.contains(2005);

        // then
        assertThat(result).isFalse().as(() -> "2005 doesn't exist in list, but list.contains(2005) return true");
    }

    @Test
    @DisplayName("Checking the correctness of the contains method with empty list")
    void notContainsItemInEmptyListTest() {
        // given
        CustomLinkedList<Integer> list = new CustomLinkedListImpl<>();

        // when
        boolean result = list.contains(2005);

        // then
        assertThat(result).isFalse().as(() -> "list is empty, but list.contains(2005) return true");
    }

    @Test
    @DisplayName("Checking the correctness of the addAll method with other collection")
    void addAllItemsFromOtherCollectionTest() {
        // given
        CustomLinkedList<Number> list = new CustomLinkedListImpl<>();
        List<Integer> onlyOddList = IntStream.range(0, 10_000).filter((i) -> i % 2 != 0).boxed().toList();

        // when
        boolean result = list.addAll(onlyOddList);

        // then
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(list.size()).isEqualTo(onlyOddList.size()).as(() -> "in empty customList was addAll(otherList), but otherList.size() = " + onlyOddList.size() + " and customList.size() = " + list.size());
        softly.assertThat(result).isTrue().as(() -> "with usage addAll(otherList) where otherList.size() != 0 result of addAll(otherList) is false");
        for (int i = 0; i < list.size(); ++i) {
            int index = i;
            softly.assertThat(list.get(i)).isEqualTo(onlyOddList.get(i)).as(() -> "element with index " + index + " from the customList does not match the element with index " + index + " from the otherList");
        }
        softly.assertAll();
    }

    @Test
    @DisplayName("Checking the correctness of the addAll method with customList collection")
    void addAllItemsFromCustomLinkedListCollectionTest() {
        // given
        CustomLinkedList<Number> list = new CustomLinkedListImpl<>();
        CustomLinkedList<Integer> onlyOddList = IntStream.range(0, 10_000)
                .filter((i) -> i % 2 != 0)
                .boxed()
                .collect(new CustomLinkedListCollector<>());

        // when
        boolean result = list.addAll(onlyOddList);

        // then
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(list.size()).isEqualTo(onlyOddList.size()).as(() -> "in empty customList was addAll(otherList), but otherCustomList.size() = " + onlyOddList.size() + " and customList.size() = " + list.size());
        softly.assertThat(result).isTrue().as(() -> "with usage addAll(otherCustomList) where otherCustomList.size() != 0 result of addAll(otherCustomList) is false");
        for (int i = 0; i < list.size(); ++i) {
            int index = i;
            softly.assertThat(list.get(i)).isEqualTo(onlyOddList.get(i)).as(() -> "element with index " + index + " from the customList does not match the element with index " + index + " from the otherCustomList");
        }
        softly.assertAll();
    }

    @Test
    @DisplayName("Checking the correctness of the addAll method with empty collection")
    void addAllItemsFromEmptyCollectionTest() {
        // given
        CustomLinkedList<Number> list = new CustomLinkedListImpl<>();
        List<Integer> emptyList = new LinkedList<>();

        // when
        boolean result = list.addAll(emptyList);

        // then
        assertThat(result).isFalse().as(() -> "otherList is empty, but list.addAll(otherList) return true");
    }

    @Test
    @DisplayName("Checking the correctness of CustomLinkedList.of() method")
    void customLinkedListOfTest() {
        // given
        int a = 1;
        int b = 2;
        int c = 3;

        // when
        CustomLinkedList<Integer> list = CustomLinkedList.of(a, b, c);

        // then
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(list.size()).isEqualTo(3).as(() -> "was added 3 elements, but list.size() " + list.size());
        softly.assertThat(list.get(0)).isEqualTo(a).as(() -> a + " was added first, but list.get(0) " + list.get(0));
        softly.assertThat(list.get(1)).isEqualTo(b).as(() -> b + " was added second, but list.get(1) " + list.get(1));
        softly.assertThat(list.get(2)).isEqualTo(c).as(() -> c + " was added third, but list.get(2) " + list.get(2));

        softly.assertAll();
    }

    @Test
    @DisplayName("Checking the correctness of toString method")
    void toStringTest() {
        // given
        CustomLinkedList<String> list = CustomLinkedList.of("T-Bank", "super", "good");

        // when
        String result = list.toString();

        // then
        assertThat(result).isEqualTo("[ T-Bank super good ]").as(() -> "list contains only \"T-Bank\", \"super\", \"good\" elements, but list.toString " + result);
    }
}
