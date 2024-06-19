package gitlet;

import java.io.*;
import java.util.List;

public class Remote implements Serializable {
    /** The remote directory of the remote. */
    private File REMOTE_DIR;
    /** The remote name of the remote. */
    private String remoteName;

    public Remote(String remoteName, String Path) {
        this.remoteName = remoteName;
        REMOTE_DIR = Utils.join(Path);
    }

    public File getRemoteDir() {
        return REMOTE_DIR;
    }

    public String getRemoteName() {
        return remoteName;
    }

    /** Get the remote branch name. */
    public List<String> getRemoteBranches() {
        return Utils.plainFilenamesIn(REMOTE_DIR);
    }

}
