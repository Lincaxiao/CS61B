package gitlet;

import java.io.*;
import java.util.Arrays;


/**
 * This class is used to represent the detailed information of a single blob/file.
 */
public class Blobs implements Serializable {

    /** The directory of all the blobs. */
    public static final File BLOB_DIR = Utils.join(Repository.GITLET_DIR, "objects");
    /** The content of this blob. */
    private byte[] content;
    /** The SHA-1 Hash of this blob and file name. */
    private String blobId;

    public Blobs(byte[] content) {
        this.content = content;
        this.blobId = Utils.sha1(content);
    }

    public void saveBlob() {
        File blobFile = Utils.join(BLOB_DIR, blobId);
        Utils.writeObject(blobFile, this);
    }

    public String getBlobId() {
        return blobId;
    }

    public byte[] getContent() {
        return content;
    }

}
