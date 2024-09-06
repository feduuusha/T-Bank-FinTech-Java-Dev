package org.tbank.fintech.lesson3;

import java.util.Collection;

public class CustomLinkedListImpl<E> implements CustomLinkedList<E> {

    private Node<E> head;
    private Node<E> tail;
    private int size;

    @Override
    public boolean add(E element) {
        if (this.head == null) {
            this.head = new Node<>(element, null, null);
            this.tail = this.head;
        } else {
            Node<E> newNode = new Node<>(element, null, this.tail);
            this.tail.next = newNode;
            this.tail = newNode;
        }
        ++size;
        return true;
    }

    @Override
    public E get(int index) {
        this.checkPositionIndex(index);
        return getNode(index).val;
    }

    private Node<E> getNode(int index) {
        Node<E> curr;
        if (index < this.size >> 1) {
            curr = this.head;
            for (int i = 0; i < index; ++i) {
                curr = curr.next;
            }
        } else {
            curr = this.tail;
            for (int i = this.size - 1; i > index; --i) {
                curr = curr.prev;
            }
        }
        return curr;
    }

    @Override
    public E remove(int index) {
        this.checkPositionIndex(index);
        Node<E> curr = getNode(index);
        E value = curr.val;
        Node<E> targetNext = curr.next;
        Node<E> targetPrev = curr.prev;
        if (targetNext == null) {
            this.tail = targetPrev;
        } else {
            targetNext.prev = targetPrev;
            curr.next = null;
        }

        if (targetPrev == null) {
            this.head = targetNext;
        } else {
            targetPrev.next = targetNext;
            curr.prev = null;
        }
        curr.val = null;
        --size;
        return value;
    }

    @Override
    public boolean contains(Object object) {
        if (this.size != 0) {
            Node<E> curr = this.head;
            if (object == null) {
                while (curr != null) {
                    if (curr.val == null) return true;
                    curr = curr.next;
                }
            } else {
                while (curr != null) {
                    if (curr.val.equals(object)) return true;
                    curr = curr.next;
                }
            }
        }
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        if (c == null || c.isEmpty()) {
            return false;
        }
        else {
            for (E elem : c) {
                this.add(elem);
            }
            return true;
        }
    }

    @Override
    public int size() {
        return this.size;
    }

    private void checkPositionIndex(int index) {
        if (!(index >= 0 && index < this.size)) {
            throw new IndexOutOfBoundsException("Index: " + index + ", size: " + this.size);
        }
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append("[ ");
        Node<E> node = head;
        while (node != null) {
            output.append(node.val).append(" ");
            node = node.next;
        }
        output.append("]");
        return output.toString();
    }

    private static class Node<E> {
        E val;
        Node<E> next;
        Node<E> prev;

        public Node(E val, Node<E> next, Node<E> prev) {
            this.val = val;
            this.next = next;
            this.prev = prev;
        }
    }
}
