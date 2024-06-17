package gitlet;

import java.io.*;
import java.util.Arrays;


/**
 * This class is used to represent the detailed information of a single blob/file.
 */
public class blob implements Serializable{

    /** The directory of all the blobs. */
    public static final File BLOB_DIR = Utils.join(Repository.GITLET_DIR, "objects");
    /** The content of this blob. */
    private final byte[] content;
    /** The file name of this blob. */
    private String fileName;
    /** The SHA-1 Hash of this blob. */
    private String blobId;

    public blob(byte[] content, String fileName) {
        this.content = content;
        this.fileName = fileName;
        this.blobId = Utils.sha1(Arrays.toString(content), fileName);
    }

    public String getBlobId() {
        return blobId;
    }

    public void saveBlob() {
        File blobFile = Utils.join(BLOB_DIR, blobId);
        Utils.writeObject(blobFile, this);
    }
}
