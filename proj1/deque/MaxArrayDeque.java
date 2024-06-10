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
        T max_val = this.get(0);
        for (int i = 0; i < this.size(); i++) {
            if (comp.compare(max_val, this.get(i)) < 0) {
                max_val = this.get(i);
            }
        }
        return max_val;
    }
}
