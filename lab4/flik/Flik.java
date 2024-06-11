package flik;

/** An Integer tester created by Flik Enterprises.
 * @author Josh Hug
 * */
public class Flik {
    /** @param a Value 1
     *  @param b Value 2
     *  @return Whether a and b are the same */
    public static boolean isSameNumber(int a, int b) {
        return a == b;
    }
    /* Explanation:
     * When int type is converted into Integer type,
     * java will automatically create new objects for a and b
     * if a or b is out the range from -128 to 127.
     */
}
