package gitlet;

import java.io.File;
import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    /* TODO: fill in the rest of this class. */
    public static void gitInit() {
        if (GITLET_DIR.exists()) {
            message("A Gitlet version-control system already exists in the current directory.");
            return;
        }
        GITLET_DIR.mkdir();
        
    }

    public static void gitAdd(String fileName) {

    }

    public static void gitCommit(String message) {

    }

    public static void gitRm(String fileName) {

    }

    public static void gitLog() {

    }

    public static void gitGloballog() {

    }

    public static void gitFind(String fileName) {

    }

    public static void gitStatus() {

    }

    public static void gitCheckout(String[] args) {

    }

    public static void gitBranch(String name) {

    }

    public static void gitRmbranch(String name) {

    }

    public static void gitReset(String name) {

    }

    public static void gitMerge(String name) {

    }

    public static void gitAddremote(String name, String path) {

    }

    public static void gitRmremote(String name) {

    }

    public static void gitPush(String repoName, String branchName) {

    }
}
