package deque;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private int size;
    private T[] tArray;
    private int length;

    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<T> {
        int curIdx;
        ArrayDequeIterator() {
            curIdx = 0;
        }
        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return tArray[curIdx++];
        }
        @Override
        public boolean hasNext() {
            return curIdx < size;
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || other.getClass() != this.getClass()) {
            return false;
        }
        ArrayDeque<T> tmp = (ArrayDeque<T>) other;
        if (tmp.size() != this.size()) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            if (this.get(i) != tmp.get(i)) {
                return false;
            }
        }
        return true;
    }
    public ArrayDeque() {
        size = 0;
        length = 8;
        tArray = (T[]) new Object[8];
    }

    private void check() {
        /* if it needs to enlarge */
        if (size + 1 == length) {
            T[] tmp = tArray;
            length *= 2;
            tArray = (T[]) new Object[length];
            System.arraycopy(tmp, 0, tArray, 0, size);
        } else if (length > 16 && size * 4 <= length) {
            T[] tmp = tArray;
            length /= 2;
            tArray = (T[]) new Object[length];
            System.arraycopy(tmp, 0, tArray, 0, size);
            check();
        }
    }
    @Override
    public void addFirst(T item) {
        check();
        T[] tmp = (T[]) new Object[length];
        System.arraycopy(tArray, 0, tmp, 1, size++);
        tmp[0] = item;
        tArray = tmp;
    }
    @Override
    public void addLast(T item) {
        check();
        tArray[size++] = item;
    }
    @Override
    public int size() {
        return size;
    }
    @Override
    public void printDeque() {
        int j = 0;
        for (int i = 0; i < size; i++, j++) {
            System.out.print(tArray[i]);
            if (j < size - 1) {
                System.out.print(" ");
            }
        }
        System.out.print("\n");
    }
    @Override
    public T removeFirst() {
        if (size() == 0) {
            return null;
        }
        T val = tArray[0];
        System.arraycopy(tArray, 1, tArray, 0, --size);
        check();
        return val;
    }
    @Override
    public T removeLast() {
        if (size() == 0) {
            return null;
        }
        T val = tArray[--size];
        tArray[size] = null;
        check();
        return val;
    }
    @Override
    public T get(int index) {
        if (index >= size) {
            return null;
        }
        return tArray[index];
    }
}
