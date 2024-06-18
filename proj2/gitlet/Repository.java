package gitlet;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Blob;
import java.util.Arrays;
import java.util.Objects;

import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *
 *  @author Zhang, Xiaochen
 */
public class Repository {
    /**
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** The objects' directory. */
    public static final File OBJECTS_DIR = join(GITLET_DIR, "objects");
    /** The refs' directory. */
    public static final File REFS_DIR = join(GITLET_DIR, "refs");
    /** The heads directory. */
    public static final File HEADS_DIR = join(REFS_DIR, "heads");
    /** The HEAD file. */
    public static final File HEAD_FILE = join(GITLET_DIR, "HEAD");
    /** The add stage directory. */
    public static final File ADD_STAGE_DIR = join(GITLET_DIR, "addstage");
    /** The remove stage directory. */
    public static final File REMOVE_STAGE_DIR = join(GITLET_DIR, "removestage");

    /**
     * The structure of the .gitlet directory:
     *   .gitlet
     *      |--objects
     *      |     |--commit and blob
     *      |--refs
     *      |    |--heads
     *      |         |--master
     *      |         |--branch1
     *      |         |--...
     *      |--HEAD
     *      |--addstage
     *      |--removestage
     * In objects directory, files' name: files' hash
     * In add/remove stage directory, files' name: original files
     * In refs/heads directory, files' name: branches' name, files' content: commits' hash
     */
    public static void gitInit() {
        if (GITLET_DIR.exists()) {
            message("A Gitlet version-control system already exists in the current directory.");
            return;
        }
        /* Create the .gitlet directory and its subdirectories. */
        GITLET_DIR.mkdir();
        ADD_STAGE_DIR.mkdir();
        REMOVE_STAGE_DIR.mkdir();
        REFS_DIR.mkdir();
        HEADS_DIR.mkdir();
        OBJECTS_DIR.mkdir();
        /* Create the master branch and set the HEAD to it. */
        Commit initialCommit = new Commit();
        initialCommit.saveCommit();
        Head head = new Head("master");
        Utils.writeObject(HEAD_FILE, head);

        /* Create the master branch and build it into the refs/heads directory. */
        File masterBranch = join(HEADS_DIR, "master");
        writeContents(masterBranch, initialCommit.getHashCode());

    }

    public static void gitAdd(String fileName) {
        // TODO: 将文件的当前版本复制到暂存区
        // TODO: 如果当前工作版本的文件与当前提交中的版本相同，则不将其暂存添加，并从暂存区中删除它（如果它已经存在）
        // TODO: 如果文件以存在于暂存区中，则更新暂存区中的文件，以便它代表当前工作版本的最新版本
        // NOTE: In objects directory, the filename is the SHA-1 hash of the file,
        // but the tree's key is the filename to the value of the SHA-1 hash.

        /* Check if the file exists in the current directory. */
        String headCommitHash = getHeadCommitHash();
        Commit headCommit = Utils.readObject(join(OBJECTS_DIR, headCommitHash), Commit.class);
        File file = new File(fileName);
        /* Check if the file exists in the current directory. */
        if (!file.exists()) {
            message("File does not exist.");
            return;
        }
        /* If so, check if the file is the same as the head commit. */
        if (headCommit.getBlobs().containsKey(fileName)) {
            byte[] content = Utils.readContents(file);
            String blobId = Utils.sha1(Arrays.toString(content), fileName);
            /* If so, do not add it to the add stage. */
            if (headCommit.getBlobs().get(fileName).equals(blobId)) {
                /* If this file is already in add stage, delete it. */
                File addStageFile = join(ADD_STAGE_DIR, fileName);
                if (addStageFile.exists()) {
                    addStageFile.delete();
                }
                return;
            }
        }

        /* If the file is not the same as the head commit, add it to the add stage. */
        File addStageFile = join(ADD_STAGE_DIR, fileName);
        byte[] content = Utils.readContents(file);
        Utils.writeContents(addStageFile, content);
    }

    public static void gitCommit(String message) {
        /* Check if the add stage is empty. */
        if (plainFilenamesIn(ADD_STAGE_DIR).isEmpty() && Utils.plainFilenamesIn(REMOVE_STAGE_DIR).isEmpty()) {
            message("No changes added to the commit.");
            System.exit(0);
        }

        /* Check if the message is empty. */
        if (message.isEmpty()) {
            message("Please enter a commit message.");
            System.exit(0);
        }

        /* Get the commit id of the head commit. */
        String currentBranch = Utils.readObject(HEAD_FILE, Head.class).getBranchName();
        String headCommitHash = Utils.readContentsAsString(join(HEADS_DIR, currentBranch));
        Commit newCommit = new Commit(message, headCommitHash);
        newCommit.saveCommit();

        /* Update the object directory. */
        for (String file : plainFilenamesIn(ADD_STAGE_DIR)) {
            File addStageFile = join(ADD_STAGE_DIR, file);
            byte[] content = Utils.readContents(addStageFile);
            Blobs newBlob = new Blobs(content);
            newBlob.saveBlob();
        }

        /* Update the head pointer to the new commit. */
        Utils.writeContents(join(HEADS_DIR, currentBranch), newCommit.getHashCode());

        /* Clear the add stage and remove stage. */
        clearStage(ADD_STAGE_DIR);
        clearStage(REMOVE_STAGE_DIR);
    }

    /**
     * Clear the stage directory.
     * @param stageDir
     */
    private static void clearStage(File stageDir) {
        for (String file : plainFilenamesIn(stageDir)) {
            join(stageDir, file).delete();
        }
    }

    public static void gitRm(String fileName) {
        /* Check if the file is in the add stage. */
        File addStageFile = join(ADD_STAGE_DIR, fileName);
        if (addStageFile.exists()) {
            addStageFile.delete();
            return;
        }

        /* Check if the file is in the head commit. */
        String headHash = getHeadCommitHash();
        Commit headCommit = Utils.readObject(join(OBJECTS_DIR, headHash), Commit.class);

        /* If the file is not in the head commit, print an error message and exit. */
        if (!headCommit.getBlobs().containsKey(fileName)) {
            message("No reason to remove the file.");
            System.exit(0);
        }

        /* If the file is in the head commit, add it to the remove stage. */
        File removeStageFile = join(REMOVE_STAGE_DIR, fileName);
        /* Just create an empty file. */
        try {
            removeStageFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /* Remove the file from the current directory, if it exists. */
        File currentFile = Utils.join(CWD, fileName);
        if (currentFile.exists()) {
            currentFile.delete();
        }
    }

    /**
     * Get the head commit hash.
     * @return
     */
    private static String getHeadCommitHash() {
        String headPointer = Utils.readObject(HEAD_FILE, Head.class).getBranchName();
        return Utils.readContentsAsString(join(HEADS_DIR, headPointer));
    }

    public static void gitLog() {
        String headCommitHash = getHeadCommitHash();
        Commit currentCommit = Utils.readObject(join(OBJECTS_DIR, headCommitHash), Commit.class);
        while (currentCommit != null) {
            currentCommit.printCommit();
            currentCommit = currentCommit.getFirstParent();
        }
    }

    public static void gitGloballog() {

    }

    public static void gitFind(String fileName) {

    }

    public static void gitStatus() {

    }

    public static void gitCheckout(String[] args) {
        switch (args.length) {
            case 2: {
                // checkout [branch name]
                break;
            }
            case 3: {
                // checkout –- [filename]

                /* Check if the operands are correct. */
                if (!args[1].equals("--")) {
                    message("Incorrect operands.");
                    System.exit(0);
                }

                String fileName = args[2];
                String headCommitHash = getHeadCommitHash();
                Commit headCommit = Utils.readObject(join(OBJECTS_DIR, headCommitHash), Commit.class);

                /* Check if the file exists in the head commit. */
                if (!headCommit.getBlobs().containsKey(fileName)) {
                    message("File does not exist in that commit.");
                    System.exit(0);
                }

                /* Restore the file to the current directory. */
                File file = join(CWD, fileName);
                byte[] content = Utils.readObject
                        (join(OBJECTS_DIR, headCommit.getBlobs().get(fileName)), Blobs.class).getContent();
                Utils.writeContents(file, content);
                break;
            }
            case 4: {
                // checkout [commit id] –- [filename]
                String fileName = args[3];
                Commit outCommit = Utils.readObject(join(OBJECTS_DIR, args[1]), Commit.class);

                /* Check if the file exists in the out commit. */
                if (!outCommit.getBlobs().containsKey(fileName)) {
                    message("File does not exist in that commit.");
                    System.exit(0);
                }

                File file = join(CWD, fileName);
                byte[] content = Utils.readContents(join(OBJECTS_DIR, outCommit.getBlobs().get(fileName)));
                Utils.writeContents(file, content);
                break;
            }
            default: {
                message("Incorrect operands.");
                System.exit(0);
            }
        }
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
