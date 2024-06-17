package gitlet;

public class Helper {
    /**
     * Accepting several arguments, return the hash of the concatenation of the arguments.
     */
    public static String getHash(Object... args) {
        StringBuilder sb = new StringBuilder();
        for (Object arg : args) {
            sb.append(arg);
        }
        return Utils.sha1(sb.toString());
    }
}
