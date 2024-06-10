package deque;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    private class Node {
        private T item;
        private Node prev;
        private Node next;

        Node() {
            this.next = this;
            this.prev = this;
        }

        Node(T item, Node prev, Node next) {
            this.item = item;
            this.prev = prev;
            this.next = next;
        }
    }

    private class LinkedIterator implements Iterator<T> {
        private Node current;

        LinkedIterator(Node sentinel) {
            current = sentinel.next;
        }

        public boolean hasNext() {
            return current != sentinel;
        }

        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            T item = current.item;
            current = current.next;
            return item;
        }
    }

    public Iterator<T> iterator() {
        return new LinkedIterator(sentinel);
    }

    private int size;
    private final Node sentinel;

    public LinkedListDeque() {
        this.sentinel = new Node();
        size = 0;
    }

    @Override
    public void addFirst(T item) {
        Node addone = new Node(item, sentinel, sentinel.next);
        /* if originally empty */
        if (size == 0) {
            sentinel.next = addone;
            sentinel.prev = addone;
            addone.next = sentinel;
            addone.prev = sentinel;
        } else {
            sentinel.next.prev = addone;
            sentinel.next = addone;
        }
        size++;
    }

    @Override
    public void addLast(T item) {
        Node addone = new Node(item, sentinel.prev, sentinel);
        /* if originally empty */
        if (size == 0) {
            sentinel.next = addone;
            sentinel.prev = addone;
            addone.next = sentinel;
            addone.prev = sentinel;
        } else {
            sentinel.prev.next = addone;
            sentinel.prev = addone;
        }
        size++;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        Node current = sentinel.next;
        for (int i = 0; i < size; i++) {
            System.out.print(current.item);
            if (i < size - 1) {
                System.out.print(" ");
            }
            current = current.next;
        }
        System.out.print("\n");
    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        Node first = sentinel.next;
        sentinel.next = first.next;
        sentinel.next.prev = sentinel;
        size--;
        return first.item;
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        Node last = sentinel.prev;
        sentinel.prev = last.prev;
        sentinel.prev.next = sentinel;
        size--;
        return last.item;
    }

    @Override
    public T get(int index) {
        if (index >= size) {
            return null;
        }
        Node current = sentinel.next;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.item;
    }

    private T getRecursiveHelper(Node node, int index) {
        if (index == 0) {
            return node.item;
        } else {
            return getRecursiveHelper(node.next, index - 1);
        }
    }

    public T getRecursive(int index) {
        if (index >= size) {
            return null;
        } else {
            return getRecursiveHelper(sentinel.next, index);
        }
    }
    public boolean isEmpty() {
        return size == 0;
    }
    @Override
    public boolean equals(Object other) {
        if (other == null || !(other instanceof Deque)) {
            return false;
        }
        Deque<T> otherDeque = (Deque<T>) other;
        if (size != otherDeque.size()) {
            return false;
        }
        for (int i = 0; i <this.size(); i++) {
            if (this.get(i) != otherDeque.get(i)) {
                return false;
            }
        }
        return true;
    }
}