package gitlet;

import java.io.*;
import java.util.Date;
import java.util.TreeMap;

/**
 * Represents a gitlet commit object does at a high level.
 *  @Author  Zhang, Xiaochen
 */
public class Commit implements Serializable{

    /** The directory of all the commits. */
    private final static  File COMMIT_DIR = Utils.join(Repository.GITLET_DIR, "objects");
    /** The message of this Commit. */
    private final String message;
    /** The time of this Commit. */
    private final Date timesTamp;
    /** The parent of this Commit. */
    private final String firstParent;
    private final String secondParent;
    /** The SHA-1 Hash of this Commit. */
    private final String hashCode;
    /** The directory of the blobs of this Commit. */
    private final TreeMap<String, String> blobs;

    /**
     * This function is used to init the git.
     */
    public Commit() {
        timesTamp = new Date(0);
        message = "initial commit";
        firstParent = null;
        secondParent = null;
        blobs = new TreeMap<>();
        hashCode = Utils.sha1(message, timesTamp.toString(), blobs.toString());
    }


    public void saveCommit() {
        File commitFile = Utils.join(COMMIT_DIR, hashCode);
        Utils.writeObject(commitFile, this);
    }

    public String  getHashCode() {
        return hashCode;
    }

    public TreeMap<String, String> getBlobs() {
        return blobs;
    }

}
