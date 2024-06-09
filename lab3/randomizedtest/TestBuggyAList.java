package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove() {
          AListNoResizing<Integer> correct = new AListNoResizing<>();
          BuggyAList<Integer> broken = new BuggyAList<>();

          correct.addLast(5);
          correct.addLast(10);
          correct.addLast(15);

          broken.addLast(5);
          broken.addLast(10);
          broken.addLast(15);

          assertEquals(correct.size(), broken.size());

          assertEquals(correct.removeLast(), broken.removeLast());
          assertEquals(correct.removeLast(), broken.removeLast());
          assertEquals(correct.removeLast(), broken.removeLast());
    }
    @Test
    public void randomtest () {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> broken = new BuggyAList<>();
        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 3);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                broken.addLast(randVal);
            } else if (operationNumber == 1) {
                // size
                int sizeL = L.size();
                int sizeB = broken.size();
                assertEquals(sizeB, sizeL);
            } else if (operationNumber == 2) {
                // removeLast
                if (L.size() > 0) {
                    assertEquals(L.removeLast(), broken.removeLast());
                }
            } else {
                if (L.size() > 0) {
                    // getLast
                    assertEquals(L.getLast(), broken.getLast());
              }
            }
        }
    }
}
