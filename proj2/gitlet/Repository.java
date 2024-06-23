package gitlet;

import java.io.*;
import java.util.*;
import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *
 *  @author Zhang, Xiaochen
 */
public class Repository {
    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** The gitlet file. */
    public static final File GITLET_FILE = join(GITLET_DIR, "gitlet");
    /** The objects' directory. */
    public static final File OBJECTS_DIR = join(GITLET_DIR, "objects");
    /** The commits' directory. */
    public static final File COMMITS_DIR = join(OBJECTS_DIR, "commits");
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
    /** The remote directory. */
    public static final File REMOTE_DIR = join(GITLET_DIR, "remotes");

    /**
     * The structure of the .gitlet directory:
     *   .gitlet
     *      |--gitlet.file
     *      |--objects
     *      |     |--commits ; patch for global-log :(
     *      |     |--commit and blob
     *      |--refs
     *      |    |--heads
     *      |         |--master
     *      |         |--branch1
     *      |         |--...
     *      |--HEAD.file
     *      |--addstage
     *      |--removestage
     *      |--remotes
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
        COMMITS_DIR.mkdir();
        REMOTE_DIR.mkdir();
        /* Create the master branch and set the HEAD to it. */
        Commit initialCommit = new Commit();
        initialCommit.saveCommit();
        Head head = new Head("master");
        Utils.writeObject(HEAD_FILE, head);

        /* Create the master branch and build it into the refs/heads directory. */
        File masterBranch = join(HEADS_DIR, "master");
        writeContents(masterBranch, initialCommit.getHashCode());

        /* Save the Gitlet object. */
        Gitlet gitlet = new Gitlet();
        gitlet.addCommit(initialCommit);
        gitlet.save();
    }

    public static void gitAdd(String fileName) {
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
            String blobId = Utils.sha1(content);
            /* If so, do not add it to the add stage. */
            if (headCommit.getBlobs().get(fileName).equals(blobId)) {
                /* If this file is already in add stage, delete it. */
                File addStageFile = join(ADD_STAGE_DIR, fileName);
                if (addStageFile.exists()) {
                    addStageFile.delete();
                }

                /* If this file is already in remove stage, delete it. */
                File removeStageFile = join(REMOVE_STAGE_DIR, fileName);
                if (removeStageFile.exists()) {
                    removeStageFile.delete();
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
        if (plainFilenamesIn(ADD_STAGE_DIR).isEmpty()
                && Utils.plainFilenamesIn(REMOVE_STAGE_DIR).isEmpty()) {
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

        /* Update the gitlet file. */
        Gitlet gitlet = Gitlet.load();
        gitlet.addCommit(newCommit);
        gitlet.save();

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
     * @return the head commit hash.
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
        for (String commitHash : plainFilenamesIn(COMMITS_DIR)) {
            Commit commit = Utils.readObject(join(COMMITS_DIR, commitHash), Commit.class);
            commit.printCommit();
        }
    }

    public static void gitFind(String message) {
        /* Check if the message is empty. */
        if (message.isEmpty()) {
            message("Please enter a commit message.");
            System.exit(0);
        }

        int count = 0;
        for (String commitHash : plainFilenamesIn(COMMITS_DIR)) {
            Commit commit = Utils.readObject(join(COMMITS_DIR, commitHash), Commit.class);
            if (commit.getMessage().equals(message)) {
                System.out.println(commit.getHashCode());
                count = 1;
            }
        }
        /* If no commit is found, print an error message. */
        if (count == 0) {
            message("Found no commit with that message.");
        }
    }

    public static void gitStatus() {
        /* Print the branches area. */
        System.out.println("=== Branches ===");
        /* Get the current branch. */
        String currentBranch = Utils.readObject(HEAD_FILE, Head.class).getBranchName();
        for (String branch : plainFilenamesIn(HEADS_DIR)) {
            if (branch.contains("_")) {
                branch = branch.replace("_", "/");
            }
            if (branch.equals(currentBranch)) {
                System.out.println("*" + branch);
            } else {
                System.out.println(branch);
            }
        }

        /* Print the staged area. */
        System.out.println("\n=== Staged Files ===");
        for (String file : plainFilenamesIn(ADD_STAGE_DIR)) {
            System.out.println(file);
        }

        /* Print the removal area. */
        System.out.println("\n=== Removed Files ===");
        for (String file : plainFilenamesIn(REMOVE_STAGE_DIR)) {
            System.out.println(file);
        }

        /* Print the modifications area. */
        System.out.println("\n=== Modifications Not Staged For Commit ===");
        /* Print the modified files unstaged for commit and their status. */
        Commit headCommit = Utils.readObject
                (join(OBJECTS_DIR, getHeadCommitHash()), Commit.class);

        for (String file : plainFilenamesIn(CWD)) {
            /* If the file is modified in the working directory, but not staged. */
            /* This time the blobs have the file name as key, but the hash-value is different. */
            if (headCommit.getBlobs().containsKey(file)) {
                byte[] content = Utils.readContents(join(CWD, file));
                String blobId = Utils.sha1(content);
                if (!headCommit.getBlobs().get(file).equals(blobId)) {
                    System.out.println(file + " (modified)");
                }
            }
        }
        /* If the file is deleted in the working directory, but not staged. */
        for (String file : headCommit.getBlobs().keySet()) {
            if (!Utils.join(CWD, file).exists()
                    && !plainFilenamesIn(REMOVE_STAGE_DIR).contains(file)) {
                System.out.println(file + " (deleted)");
            }
        }


        /* Print the untracked area. */
        System.out.println("\n=== Untracked Files ===");
        /* Print the untracked files. */
        for (String file : plainFilenamesIn(CWD)) {
            if (!headCommit.getBlobs().containsKey(file)
                    && !plainFilenamesIn(ADD_STAGE_DIR).contains(file)) {
                System.out.println(file);
            }
        }
    }

    public static void gitCheckout(String[] args) {
        switch (args.length) {
            case 2: {
                checkOutwithtwooprands(args);
                break;
            }
            case 3: {
                checkOutwiththreeoprands(args);
                break;
            }
            case 4: {
                checkOutwithfouroprands(args);
                break;
            }
            default: {
                message("Incorrect operands.");
                System.exit(0);
            }
        }
    }

    private static void checkOutwithtwooprands(String[] args) {
        // checkout [branch name]
        String branchName = args[1];
        if (branchName.contains("/")) {
            branchName = branchName.replace("/", "_");
        }
        /* Check if the branch exists. */
        if (!plainFilenamesIn(HEADS_DIR).contains(branchName)) {
            message("No such branch exists.");
            System.exit(0);
        }
        /* Check if the branch is the current branch. */
        String currentBranch = Utils.readObject(HEAD_FILE, Head.class).getBranchName();
        if (currentBranch.equals(branchName)) {
            message("No need to checkout the current branch.");
            System.exit(0);
        }
        /*
         * Check if a working file is untracked in the current
         * branch and would be overwritten by the checkout
         */
        Commit headCommit = Utils.readObject
                (join(OBJECTS_DIR, getHeadCommitHash()), Commit.class);
        for (String file : plainFilenamesIn(CWD)) {
            if (!headCommit.getBlobs().containsKey(file)
                    && !plainFilenamesIn(ADD_STAGE_DIR).contains(file)) {
                message("There is an untracked file in the way; "
                        + "delete it, or add and commit it first.");
                System.exit(0);
            }
        }
        /* Update the head pointer to the new branch. */
        Head head = new Head(branchName);
        Utils.writeObject(HEAD_FILE, head);

        /* Update the gitlet file. */
        Gitlet gitlet = Gitlet.load();
        gitlet.checkoutBranch(branchName);
        gitlet.save();

        /* Restore the files in the new branch to the current directory. */
        String newHeadCommitHash = Utils.readContentsAsString(join(HEADS_DIR, branchName));
        Commit newHeadCommit = Utils.readObject
                (join(OBJECTS_DIR, newHeadCommitHash), Commit.class);
        for (String file : plainFilenamesIn(CWD)) {
            if (!newHeadCommit.getBlobs().containsKey(file)) {
                Utils.join(CWD, file).delete();
            }
        }
        for (String file : newHeadCommit.getBlobs().keySet()) {
            File newFile = Utils.join(CWD, file);
            Blobs blob = Utils.readObject(join(OBJECTS_DIR,
                    newHeadCommit.getBlobs().get(file)), Blobs.class);
            Utils.writeContents(newFile, blob.getContent());
        }
    }

    private static void checkOutwiththreeoprands(String[] args) {
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
        String blobHash = headCommit.getBlobs().get(fileName);
        Blobs blob = Utils.readObject(join(OBJECTS_DIR, blobHash), Blobs.class);
        Utils.writeContents(file, blob.getContent());
    }

    private static String findIdWithUid(String uid) {
        for (String commitId : plainFilenamesIn(COMMITS_DIR)) {
            if (commitId.startsWith(uid)) {
                return commitId;
            }
        }
        return null;
    }
    private static void checkOutwithfouroprands(String[] args) {
        // checkout [commit id] –- [filename]
        String fileName = args[3];
        String commitId = findIdWithUid(args[1]);
        /* Check if the commit id exists. */
        if (!plainFilenamesIn(COMMITS_DIR).contains(commitId)) {
            message("No commit with that id exists.");
            System.exit(0);
        }
        Commit outCommit = Utils.readObject(join(OBJECTS_DIR, commitId), Commit.class);
        /* Check if the operands are correct. */
        if (!args[2].equals("--")) {
            message("Incorrect operands.");
            System.exit(0);
        }

        /* Check if the file exists in the out commit. */
        if (!outCommit.getBlobs().containsKey(fileName)) {
            message("File does not exist in that commit.");
            System.exit(0);
        }
        /* Restore the file to the current directory. */
        File file = join(CWD, fileName);
        byte[] content = Utils.readObject
                (join(OBJECTS_DIR, outCommit.getBlobs().get(fileName)), Blobs.class).getContent();
        Utils.writeContents(file, content);
    }

    public static void gitBranch(String name) {
        if (name.contains("/")) {
            name = name.replace("/", "_");
        }
        /* Check if the branch exists. */
        if (plainFilenamesIn(HEADS_DIR).contains(name)) {
            message("A branch with that name already exists.");
            System.exit(0);
        }
        /* Create a new branch and set the head pointer to it. */
        Utils.writeContents(join(HEADS_DIR, name), getHeadCommitHash());

        /* Update the gitlet file. */
        Gitlet gitlet = Gitlet.load();
        gitlet.createBranch(name);
        Branch currentBranch = gitlet.getCurrentBranch();
        Branch newBranch = gitlet.getBranch(name);
        newBranch.getMergedBranches().addAll(currentBranch.getMergedBranches());
        gitlet.save();
    }

    public static void gitRmbranch(String name) {
        if (name.contains("/")) {
            name = name.replace("/", "_");
        }
        /* Check if the branch exists. */
        if (!plainFilenamesIn(HEADS_DIR).contains(name)) {
            message("A branch with that name does not exist.");
            System.exit(0);
        }
        /* Check if the branch is the current branch. */
        String currentBranch = Utils.readObject(HEAD_FILE, Head.class).getBranchName();
        if (currentBranch.equals(name)) {
            message("Cannot remove the current branch.");
            System.exit(0);
        }
        /* Remove the branch. */
        join(HEADS_DIR, name).delete();

        /* Update the gitlet file. */
        Gitlet gitlet = Gitlet.load();
        gitlet.removeBranch(name);
        gitlet.save();
    }

    /**
     * Reset the head pointer to the commit with the given id.
     * @param commitId
     */
    public static void gitReset(String commitId) {
        /* Check if the commit id exists. */
        if (!plainFilenamesIn(COMMITS_DIR).contains(commitId)) {
            message("No commit with that id exists.");
            System.exit(0);
        }
        /* Check if the staging area is clean. */
        Commit headCommit = Utils.readObject
                (join(OBJECTS_DIR, getHeadCommitHash()), Commit.class);
        for (String file : plainFilenamesIn(CWD)) {
            if (!headCommit.getBlobs().containsKey(file)
                    && !plainFilenamesIn(ADD_STAGE_DIR).contains(file)) {
                message("There is an untracked file in the way; "
                        + "delete it, or add and commit it first.");
                System.exit(0);
            }
        }
        /* Get the commit. */
        Commit resetCommit = Utils.readObject(join(OBJECTS_DIR, commitId), Commit.class);
        /* Update the head pointer to the commit. */
        String currentBranch = Utils.readObject(HEAD_FILE, Head.class).getBranchName();
        Utils.writeContents(join(HEADS_DIR, currentBranch), commitId);
        /* Restore the files to the current directory. */
        for (String file : plainFilenamesIn(CWD)) {
            if (!resetCommit.getBlobs().containsKey(file)) {
                Utils.join(CWD, file).delete();
            }
        }
        for (String file : resetCommit.getBlobs().keySet()) {
            File newFile = Utils.join(CWD, file);
            Blobs blob = Utils.readObject
                    (join(OBJECTS_DIR, resetCommit.getBlobs().get(file)), Blobs.class);
            Utils.writeContents(newFile, blob.getContent());
        }
        /* Clear the add stage and remove stage. */
        clearStage(ADD_STAGE_DIR);
        clearStage(REMOVE_STAGE_DIR);

        /* Update the gitlet file. */
        /* Update the gitlet object. */
        Gitlet gitlet = Gitlet.load();
        gitlet.setCurrentBranch(currentBranch);
        gitlet.updateBranchHead(currentBranch, commitId);
        gitlet.save();
    }

    /**
     * Find the split point of the current branch and the given branch.
     * @param branchName the given branch needs to be merged.
     * @return the commit id of the split point.
     */
    private static String findSplitPointId(String branchName) {
        if (branchName.contains("/")) {
            branchName = branchName.replace("/", "_");
        }
        /* Check if the staging area is clean. */
        if (!plainFilenamesIn(ADD_STAGE_DIR).isEmpty()
                || !plainFilenamesIn(REMOVE_STAGE_DIR).isEmpty()) {
            message("You have uncommitted changes.");
            System.exit(0);
        }

        String currentBranch = Utils.readObject(HEAD_FILE, Head.class).getBranchName();
        String currentCommitId = Utils.readContentsAsString(join(HEADS_DIR, currentBranch));
        /* Check if the given branch exists. */
        if (!plainFilenamesIn(HEADS_DIR).contains(branchName)) {
            message("A branch with that name does not exist.");
            System.exit(0);
        }
        /* Check if the given branch is the current branch. */
        if (currentBranch.equals(branchName)) {
            message("Cannot merge a branch with itself.");
            System.exit(0);
        }
        /* Find the split point. */
        Gitlet gitlet = Gitlet.load();
        Commit splitPoint = gitlet.findSplitPoint(currentBranch, branchName);
        return splitPoint.getHashCode();
    }

    /**
     * Deal with the special cases of merging.
     * 1. The split point has the same content as the given branch.
     * 2. The split point has the same content as the current branch.
     */
    private static void mergeSpecialCases(String splitPointCommitId, String branchName) {
        if (branchName.contains("/")) {
            branchName = branchName.replace("/", "_");
        }
        /* Deal with the Case 1: The split point has the same content as the given branch. */
        String branchCommit = Utils.readContentsAsString(join(HEADS_DIR, branchName));
        if (splitPointCommitId.equals(branchCommit)) {
            message("Given branch is an ancestor of the current branch.");
            System.exit(0);
        }

        /* Deal with the Case 2: The split point has the same content as the current branch. */
        String currentBranch = Utils.readObject(HEAD_FILE, Head.class).getBranchName();
        String currentCommit = Utils.readContentsAsString(join(HEADS_DIR, currentBranch));
        if (splitPointCommitId.equals(currentCommit)) {
            gitCheckout(new String[]{"checkout", branchName});
            message("Current branch fast-forwarded.");
            System.exit(0);
        }
    }

    public static void gitMerge(String branchName) {
        if (branchName.contains("/")) {
            branchName = branchName.replace("/", "_");
        }
        String splitPointCommitId = findSplitPointId(branchName);
        mergeSpecialCases(splitPointCommitId, branchName);
        String currentBranchName = Utils.readObject(HEAD_FILE, Head.class).getBranchName();
        String currentCommitId = Utils.readContentsAsString(join(HEADS_DIR, currentBranchName));
        String givenCommitId = Utils.readContentsAsString(join(HEADS_DIR, branchName));
        Commit splitPoint = Utils.readObject(join(OBJECTS_DIR, splitPointCommitId), Commit.class);
        Commit currentCommit = Utils.readObject(join(OBJECTS_DIR, currentCommitId), Commit.class);
        Commit givenCommit = Utils.readObject(join(OBJECTS_DIR, givenCommitId), Commit.class);
        List<String> conflictFiles = new ArrayList<>();
        /* Revise the files in the current directory. */
        for (String file : plainFilenamesIn(CWD)) {
            if (!currentCommit.getBlobs().containsKey(file)
                    && !plainFilenamesIn(ADD_STAGE_DIR).contains(file)) {
                message("There is an untracked file in the way; "
                        + "delete it, or add and commit it first.");
                System.exit(0);
            }
        }
        /* Create a fileSet contains all the file names in split point,
         * current commit and given commit wirhout repeat
         */
        Set<String> fileSet = new HashSet<>();
        fileSet.addAll(splitPoint.getBlobs().keySet());
        fileSet.addAll(currentCommit.getBlobs().keySet());
        fileSet.addAll(givenCommit.getBlobs().keySet());

        for (String file : fileSet) {
            String spId = splitPoint.getBlobs().get(file);
            String curId = currentCommit.getBlobs().get(file);
            String givId = givenCommit.getBlobs().get(file);
            if (spId == null) {
                if (curId == null) {
                    gitCheckout(new String[]{"checkout", givenCommitId, "--", file});
                    gitAdd(file);
                } else if (!curId.equals(givId) && givId != null) {
                    conflictFiles.add(file);
                }
            } else {
                if (curId != null && givId != null) {
                    if (spId.equals(curId) && !spId.equals(givId)) {
                        gitCheckout(new String[]{"checkout", givenCommitId, "--", file});
                        gitAdd(file);
                    } else if (!spId.equals(curId) && !spId.equals(givId) && !curId.equals(givId)) {
                        conflictFiles.add(file);
                    }
                } else if (curId == null && givId != null) {
                    if (!spId.equals(givId)) {
                        conflictFiles.add(file);
                    }
                } else if (curId != null && givId == null) {
                    if (!spId.equals(curId)) {
                        conflictFiles.add(file);
                    } else {
                        Utils.join(CWD, file).delete();
                        gitRm(file);
                    }
                }
            }
        }
        if (!conflictFiles.isEmpty()) {
            message("Encountered a merge conflict.");
            for (String file : conflictFiles) {
                processConflict(file, currentCommit, givenCommit);
            }
        }
        if (branchName.contains("_")) {
            branchName = branchName.replace("_", "/");
        }
        gitCommitCaseMerge("Merged " + branchName + " into "
                + currentBranchName + ".", givenCommitId);
        /* Update the gitlet file. */
        Gitlet gitlet = Gitlet.load();
        gitlet.getCurrentBranch().addMergedBranch(branchName);
        gitlet.save();
    }

    private static void processConflict(String fileName, Commit currentCommit, Commit givenCommit) {
        String curId = currentCommit.getBlobs().get(fileName);
        String givId = givenCommit.getBlobs().get(fileName);
        File file = Utils.join(CWD, fileName);
        String content = "<<<<<<< HEAD\n";
        if (curId == null) {
            content += "=======\n";
        } else {
            File curFile = Utils.join(OBJECTS_DIR, curId);
            /* It needs to convert the byte[] to String. */
            StringBuilder sb = new StringBuilder();
            for (byte b : Utils.readObject(curFile, Blobs.class).getContent()) {
                sb.append((char) b);
            }
            content += sb.toString() + "=======\n";
        }
        if (givId == null) {
            content += ">>>>>>>\n";
        } else {
            File givFile = Utils.join(OBJECTS_DIR, givId);
            /* It needs to convert the byte[] to String. */
            StringBuilder sb = new StringBuilder();
            for (byte b : Utils.readObject(givFile, Blobs.class).getContent()) {
                sb.append((char) b);
            }
            content += sb.toString() + ">>>>>>>\n";
        }

        Utils.writeContents(file, content);
        gitAdd(fileName);
    }

    public static void gitCommitCaseMerge(String message, String givenCommitId) {
        /* Check if the add stage is empty. */
        if (plainFilenamesIn(ADD_STAGE_DIR).isEmpty()
                && Utils.plainFilenamesIn(REMOVE_STAGE_DIR).isEmpty()) {
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
        newCommit.setSecondParent(givenCommitId);
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

        /* Update the gitlet file. */
        Gitlet gitlet = Gitlet.load();
        gitlet.addCommit(newCommit);
        gitlet.save();

        /* Clear the add stage and remove stage. */
        clearStage(ADD_STAGE_DIR);
        clearStage(REMOVE_STAGE_DIR);
    }

    public static void gitAddremote(String name, String path) {
        File remoteFile = Utils.join(REMOTE_DIR, name);

        /* Check if the remote name exists. */
        if (remoteFile.exists()) {
            message("A remote with that name already exists.");
            System.exit(0);
        }

        /* Add the remote. */
        Remote remote = new Remote(name, path);
        Utils.writeObject(remoteFile, remote);
    }

    public static void gitRmremote(String name) {
        File remoteFile = Utils.join(REMOTE_DIR, name);

        /* Check if the remote name exists. */
        if (!remoteFile.exists()) {
            message("A remote with that name does not exist.");
            System.exit(0);
        }

        /* Remove the remote. */
        remoteFile.delete();
    }

    public static void gitPush(String repoName, String branchName) {
        /* Check if the remote name exists. */
        File remoteFile = Utils.join(REMOTE_DIR, repoName);
        Remote remote = Utils.readObject(remoteFile, Remote.class);

        if (!remote.getRemoteDir().exists()) {
            message("Remote directory not found.");
            System.exit(0);
        }

        /* check if the remote head in the branch of current local head */
        File remoteHeadFile = Utils.join(remote.getRemoteDir(), "refs", "heads", branchName);
        String remoteHeadId = Utils.readContentsAsString(remoteHeadFile);
        Gitlet gitlet = Gitlet.load();
        Branch currentBranch = gitlet.getCurrentBranch();
        List<Commit> currentCommits = currentBranch.getCommits();
        if (!checkRemoteHead(remoteHeadId, currentCommits)) {
            message("Please pull down remote changes before pushing.");
            System.exit(0);
        }

        /* Update the remote head to the current head. */
        File localHeadFile = Utils.join(HEADS_DIR, branchName);
        Utils.writeContents(remoteHeadFile, Utils.readContentsAsString(localHeadFile));

        /* Update the remote gitlet file. */
        File remoteGitletFile = Utils.join(remote.getRemoteDir(), "gitlet");
        Gitlet remoteGitlet = Utils.readObject(remoteGitletFile, Gitlet.class);
        boolean isFuture = false;
        for (Commit commit : currentCommits) {
            if (isFuture) {
                remoteGitlet.addCommit(commit);
                /* Update the object directory. */
                for (String file : commit.getBlobs().keySet()) {
                    File blobFile = Utils.join(OBJECTS_DIR, commit.getBlobs().get(file));
                    Blobs blob = Utils.readObject(blobFile, Blobs.class);
                    File remoteBlobFile = Utils.join(remote.getRemoteDir(), "objects");
                    remoteBlobFile = Utils.join(remoteBlobFile, commit.getBlobs().get(file));
                    Utils.writeObject(remoteBlobFile, blob);
                }
                /* Update the commit directory. */
                File commitFile = Utils.join(remote.getRemoteDir(), "objects", "commits");
                File objectFile = Utils.join(remote.getRemoteDir(), "objects");
                objectFile = Utils.join(objectFile, commit.getHashCode());
                commitFile = Utils.join(commitFile, commit.getHashCode());
                Utils.writeObject(commitFile, commit);
                Utils.writeObject(objectFile, commit);
            }
            if (commit.getHashCode().equals(remoteHeadId)) {
                isFuture = true;
            }
        }
        /* Update the remote branch in the remote gitlet file. */
        Utils.writeObject(remoteGitletFile, remoteGitlet);
    }

    private static boolean checkRemoteHead(String remoteHeadId, List<Commit> currentCommits) {
        for (Commit commit : currentCommits) {
            if (commit.getHashCode().equals(remoteHeadId)) {
                return true;
            }
        }
        return false;
    }

    public static void gitFetch(String repoName, String branchName) {
        /* Check if the remote name exists. */
        File remoteFile = Utils.join(REMOTE_DIR, repoName);
        Remote remote = Utils.readObject(remoteFile, Remote.class);
        String localBranchName = repoName + "_" + branchName;
        if (!remote.getRemoteDir().exists()) {
            message("Remote directory not found.");
            System.exit(0);
        }

        /* Check if the remote branch exists. */
        File remoteHeadFile = Utils.join(remote.getRemoteDir(), "refs", "heads", branchName);
        if (!remoteHeadFile.exists()) {
            message("That remote does not have that branch.");
            System.exit(0);
        }

        /* Update the local gitlet file. */

        Gitlet gitlet = Gitlet.load();
        File remoteGitletFile = Utils.join(remote.getRemoteDir(), "gitlet");
        Gitlet remoteGitlet = Utils.readObject(remoteGitletFile, Gitlet.class);
        gitlet.createBranch(localBranchName);
        Branch newBranch = gitlet.getBranch(localBranchName);
        newBranch.getMergedBranches().addAll(remoteGitlet.getCurrentBranch().getMergedBranches());
        List<Commit> currentCommits = newBranch.getCommits();
        List<Commit> remoteCommits = remoteGitlet.getBranch(branchName).getCommits();
        currentCommits.clear();
        currentCommits.addAll(remoteCommits);

        /* Copy the commits and blobs from the remote gitlet file to the local gitlet file. */
        for (Commit commit : remoteGitlet.getBranch(branchName).getCommits()) {
            File commitFile = Utils.join(OBJECTS_DIR, commit.getHashCode());
            Utils.writeObject(commitFile, commit);
            commitFile = Utils.join(COMMITS_DIR, commit.getHashCode());
            Utils.writeObject(commitFile, commit);
            for (String file : commit.getBlobs().keySet()) {
                File remoteBlobFile = Utils.join(remote.getRemoteDir(), "objects");
                remoteBlobFile = Utils.join(remoteBlobFile, commit.getBlobs().get(file));
                File blobFile = Utils.join(OBJECTS_DIR, commit.getBlobs().get(file));
                Blobs blob = Utils.readObject(remoteBlobFile, Blobs.class);
                Utils.writeObject(blobFile, blob);
            }
        }

        /* Replace the "/" with "_" in the branch name. */
        String localBranch = localBranchName.replace("/", "_");
        /* Update the head/branch pointer to the new commit. */
        File branchFile = Utils.join(HEADS_DIR,  localBranch);
        Utils.writeContents(branchFile, Utils.readContentsAsString(remoteHeadFile));
        gitlet.save();
    }


    public static void gitPull(String repoName, String branchName) {
        gitFetch(repoName, branchName);
        gitMerge(repoName + "/" + branchName);
    }
}
