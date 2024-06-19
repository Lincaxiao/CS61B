package gitlet;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Represents a gitlet commit object does at a high level.
 *  @Author Zhang, Xiaochen
 */
public class Commit implements Serializable {

    /** The directory of all the commits. */
    private static File COMMIT_DIR = Utils.join(Repository.GITLET_DIR, "objects");
    /** The message of this Commit. */
    private final String message;
    /** The time of this Commit. */
    private final Date timesTamp;
    /** The parent of this Commit. */
    private final String firstParent;
    private String secondParent;
    /** The SHA-1 Hash of this Commit. */
    private String hashCode;
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

    public Commit(String massage, String firstParent) {
        this.message = massage;
        timesTamp = new Date();
        this.firstParent = firstParent;
        this.secondParent = null;
        this.blobs = new TreeMap<>();
        /* Now add/remove the blobs */
        getUpdatedBlobs();
        hashCode = Utils.sha1(message, timesTamp.toString(), blobs.toString());
    }

    private void getUpdatedBlobs() {
        if (firstParent != null) {
            Commit parent = Utils.readObject(Utils.join(COMMIT_DIR, firstParent), Commit.class);
            blobs.putAll(parent.blobs);
            /* now add/revise files appeared in the add stage */
            File addStage = Repository.ADD_STAGE_DIR;
            for (String file : Utils.plainFilenamesIn(addStage)) {
                blobs.put(file, Utils.sha1(Utils.readContents(Utils.join(addStage, file))));
            }
            /* now remove files appeared in the remove stage */
            File removeStage = Repository.REMOVE_STAGE_DIR;
            for (String file : Utils.plainFilenamesIn(removeStage)) {
                blobs.remove(file);
            }
        }
    }
    public void saveCommit() {
        File commitFile = Utils.join(COMMIT_DIR, hashCode);
        Utils.writeObject(commitFile, this);
        /* bad patch for global log :( */
        commitFile = Utils.join(COMMIT_DIR, "commits", hashCode);
        Utils.writeObject(commitFile, this);
    }

    public String  getHashCode() {
        return hashCode;
    }

    public TreeMap<String, String> getBlobs() {
        return blobs;
    }

    public String getMessage() {
        return message;
    }

    public Commit getFirstParent() {
        if (firstParent == null) {
            return null;
        }
        File parentCommit = Utils.join(COMMIT_DIR, this.firstParent);
        return Utils.readObject(parentCommit, Commit.class);
    }

    public Commit getSecondParent() {
        if (secondParent == null) {
            return null;
        }
        File parentCommit = Utils.join(COMMIT_DIR, this.secondParent);
        return Utils.readObject(parentCommit, Commit.class);
    }

    public void printCommit() {
        System.out.println("===");
        System.out.println("commit " + hashCode);
        System.out.println("Date: " + dateToTimeStamp(timesTamp));
        System.out.println(message);
        System.out.println();
    }

    private static String dateToTimeStamp(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        return dateFormat.format(date);
    }

    public Date getTimestamp() {
        return timesTamp;
    }

    public void setSecondParent(String secondParent) {
        this.secondParent = secondParent;
    }
}
