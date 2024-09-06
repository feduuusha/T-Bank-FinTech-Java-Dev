package org.tbank.fintech.lesson3;

import java.util.List;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) {
//        part 2 (look test package)
        CustomLinkedList<Integer> list = new CustomLinkedListImpl<>();
        list.add(1);
        System.out.println(list.get(0));
        System.out.println(list.remove(0));
        System.out.println(list.contains(0));
        List<Integer> listInt = List.of(5, 6, 7, 9, 10);
        list.addAll(listInt);
        System.out.println(list);
//        part 3
        CustomLinkedList<Integer> customLinkedList =
                IntStream.range(0, 1001)
                .filter(i -> i % 2 == 0)
                .boxed()
                .reduce(new CustomLinkedListImpl<>(),
                (customList, element) -> {
                    customList.add(element);
                    return customList;
                },
                (customList1, customList2) -> {
                    for (int i = 0; i < customList2.size(); ++i) customList1.add(customList2.get(i));
                    return customList1;
                }
        );
        System.out.println(customLinkedList);
    }
}
