package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> implements Comparator<T>{
    private Comparator<T> comp;
    @Override
    public int compare(T o1, T o2) {
        return comp.compare(o1, o2);
    }

    public MaxArrayDeque(Comparator<T> c) {
        comp = c;
    }

    public T max() {
        if (this.size() == 0) {
            return null;
        }
        T maxVal = this.get(0);
        for (int i = 0; i < this.size(); i++) {
            if (comp.compare(maxVal, this.get(i)) < 0) {
                maxVal = this.get(i);
            }
        }
        return maxVal;
    }

    public T max(Comparator<T> c) {
        if (this.size() == 0) {
            return null;
        }
        T maxVal = this.get(0);
        for (int i = 0; i < this.size(); i++) {
            if (c.compare(maxVal, this.get(i)) < 0) {
                maxVal = this.get(i);
            }
        }
        return maxVal;
    }
}
