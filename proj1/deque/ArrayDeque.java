package deque;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private int size;
    private int front;
    private int rear;
    private T[] tArray;

    public ArrayDeque() {
        size = 0;
        front = 0;
        rear = 0;
        tArray = (T[]) new Object[8];
    }

    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<T> {
        int curIdx;
        int count;

        ArrayDequeIterator() {
            curIdx = front;
            count = 0;
        }

        @Override
        public boolean hasNext() {
            return count < size;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            T item = tArray[curIdx];
            curIdx = (curIdx + 1) % tArray.length;
            count++;
            return item;
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || !(other instanceof Deque)) {
            return false;
        }
        Deque<T> otherDeque = (Deque<T>) other;
        if (this.size() != otherDeque.size()) {
            return false;
        }
        Iterator<T> thisIterator = this.iterator();
        Iterator<T> otherIterator = otherDeque.iterator();
        while (thisIterator.hasNext() && otherIterator.hasNext()) {
            T thisItem = thisIterator.next();
            T otherItem = otherIterator.next();
            if (!thisItem.equals(otherItem)) {
                return false;
            }
        }
        return true;
    }

    private void resize(int newCapacity) {
        T[] newArray = (T[]) new Object[newCapacity];
        for (int i = 0; i < size; i++) {
            newArray[i] = tArray[(front + i) % tArray.length];
        }
        tArray = newArray;
        front = 0;
        rear = size;
    }

    @Override
    public void addFirst(T item) {
        if (size == tArray.length) {
            resize(size * 2);
        }
        front = (front - 1 + tArray.length) % tArray.length;
        tArray[front] = item;
        size++;
    }

    @Override
    public void addLast(T item) {
        if (size == tArray.length) {
            resize(size * 2);
        }
        tArray[rear] = item;
        rear = (rear + 1) % tArray.length;
        size++;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        for (int i = 0; i < size; i++) {
            System.out.print(tArray[(front + i) % tArray.length]);
            if (i < size - 1) {
                System.out.print(" ");
            }
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        T item = tArray[front];
        tArray[front] = null;
        front = (front + 1) % tArray.length;
        size--;
        if (size > 0 && size == tArray.length / 4) {
            resize(tArray.length / 2);
        }
        return item;
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        rear = (rear - 1 + tArray.length) % tArray.length;
        T item = tArray[rear];
        tArray[rear] = null;
        size--;
        if (size > 0 && size == tArray.length / 4) {
            resize(tArray.length / 2);
        }
        return item;
    }

    public boolean isEmpty() {
        return size == 0;
    }
    @Override
    public T get(int index) {
        if (index >= size || index < 0) {
            return null;
        }
        return tArray[(front + index) % tArray.length];
    }
}