package deque;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Comparator;

public class MaxArrayDequeTest {

    @Test
    public void maxWithDefaultComparatorTest() {
        Comparator<String> lengthComparator = new Comparator<>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.length() - o2.length();
            }
        };

        MaxArrayDeque<String> mad = new MaxArrayDeque<>(lengthComparator);

        mad.addLast("apple");
        mad.addLast("pear");
        mad.addLast("banana");

        String max = mad.max();
        assertEquals("banana", max);
    }

    @Test
    public void maxWithEmptyDequeTest() {
        Comparator<Integer> comp = new Comparator<>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1 - o2;
            }
        };

        MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(comp);

        assertNull(mad.max());
    }

    @Test
    public void maxWithIntegerComparatorTest() {
        MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(new Comparator<>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1 - o2; // default comparator
            }
        });

        mad.addLast(3);
        mad.addLast(1);
        mad.addLast(5);
        mad.addLast(2);

        Integer max = mad.max();
        assertEquals((Integer) 5, max);
    }

    @Test
    public void maxWithReverseOrderComparatorTest() {
        MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(new Comparator<>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o2 - o1; // reverse order comparator
            }
        });

        mad.addLast(3);
        mad.addLast(1);
        mad.addLast(5);
        mad.addLast(2);

        Integer max = mad.max();
        assertEquals((Integer) 1, max); // should find the min value since comparator is reversed
    }
}