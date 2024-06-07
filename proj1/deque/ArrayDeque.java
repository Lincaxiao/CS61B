package deque;

public class ArrayDeque<T> {
    private int size;
    private T[] TArray;
    private int length;

    public ArrayDeque () {
        size = 0;
        length = 8;
        TArray = (T[]) new Object[8];
    }

    public boolean isEmpty () {
        return size == 0;
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

    public void addFirst (T item) {
        check();
        T[] tmp = (T[]) new Object[length];
        System.arraycopy(TArray, 0, tmp, 1, size++);
        tmp[0] = item;
        TArray = tmp;
    }

    public void addLast (T item) {
        check();
        TArray[size++] = item;
    }

    public int size() {
        return size;
    }

    public void printDeque () {
        int j = 0;
        for (int i = 0; i <size; i++, j++) {
            System.out.print(TArray[i]);
            if (j < size - 1) {
                System.out.print(" ");
            }
        }
        System.out.print("\n");
    }

    public T removeFirst () {
        if (size() == 0) {
            return null;
        }
        T val = TArray[0];
        System.arraycopy(TArray, 1, TArray, 0, --size);
        check();
        return val;
    }

    public T removeLast () {
        if (size() == 0) {
            return null;
        }
        T val = TArray[--size];
        TArray[size] = null;
        check();
        return val;
    }

    public T get (int index) {
        if (index >= size) {
            return null;
        }
        return TArray[index];
    }
}
