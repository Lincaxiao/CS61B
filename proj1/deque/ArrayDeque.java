package deque;

import org.junit.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArrayDeque<T> implements Deque<T> {
    private int size;
    private T[] TArray;
    private int length;

    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<T> {
        int cur_idx;
        public ArrayDequeIterator () {
            cur_idx = 0;
        }
        @Override
        public T next () {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return TArray[cur_idx++];
        }
        @Override
        public boolean hasNext () {
            return cur_idx < size;
        }
    }

    @Override
    public boolean equals (Object other) {
        if (other == null || other.getClass() != this.getClass()) {
            return false;
        }
        ArrayDeque<T> tmp = (ArrayDeque<T>) other;
        if (tmp.size() != this.size()) {
            return false;
        }
        for (int i = 0; i <size; i++) {
            if (this.get(i) != tmp.get(i)) {
                return false;
            }
        }
        return true;
    }
    public ArrayDeque () {
        size = 0;
        length = 8;
        TArray = (T[]) new Object[8];
    }

    private void check () {
        /* if it needs to enlarge */
        if (size + 1 == length) {
            T[] tmp = TArray;
            length *= 2;
            TArray = (T[]) new Object[length];
            System.arraycopy(tmp, 0, TArray, 0, size);
        } else if (length > 16 && size * 4 <= length) {
            T[] tmp = TArray;
            length /= 2;
            TArray = (T[]) new Object[length];
            System.arraycopy(tmp, 0, TArray, 0, size);
            check();
        }
    }
    @Override
    public void addFirst (T item) {
        check();
        T[] tmp = (T[]) new Object[length];
        System.arraycopy(TArray, 0, tmp, 1, size++);
        tmp[0] = item;
        TArray = tmp;
    }
    @Override
    public void addLast (T item) {
        check();
        TArray[size++] = item;
    }
    @Override
    public int size () {
        return size;
    }
    @Override
    public void printDeque () {
        int j = 0;
        for (int i = 0; i < size; i++, j++) {
            System.out.print(TArray[i]);
            if (j < size - 1) {
                System.out.print(" ");
            }
        }
        System.out.print("\n");
    }
    @Override
    public T removeFirst () {
        if (size() == 0) {
            return null;
        }
        T val = TArray[0];
        System.arraycopy(TArray, 1, TArray, 0, --size);
        check();
        return val;
    }
    @Override
    public T removeLast () {
        if (size() == 0) {
            return null;
        }
        T val = TArray[--size];
        TArray[size] = null;
        check();
        return val;
    }
    @Override
    public T get (int index) {
        if (index >= size) {
            return null;
        }
        return TArray[index];
    }
}
