package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;

public class Gitlet implements Serializable {
    private static final File GITLET_FILE = Utils.join(Repository.GITLET_DIR, "gitlet");
    private Map<String, Branch> branches;
    private String currentBranchName;

    public Gitlet() {
        branches = new HashMap<>();
        /* Create the initial branch "master" */
        Branch master = new Branch("master");
        branches.put("master", master);
        currentBranchName = "master";
    }

    public Map<String, Branch> getBranches() {
        return branches;
    }

    public void createBranch(String branchName) {
        Branch newBranch = new Branch(branchName);
        newBranch.getCommits().addAll
            (branches.get(currentBranchName).getCommits());
        branches.put(branchName, newBranch);
    }

    public void checkoutBranch(String branchName) {
        if (!branches.containsKey(branchName)) {
            throw new IllegalArgumentException
            ("A branch with that name does not exist.");
        }
        currentBranchName = branchName;
    }

    public void addCommit(Commit commit) {
        branches.get(currentBranchName).addCommit(commit);
    }

    public Commit findSplitPoint(String branch1, String branch2) {
        Branch b1 = branches.get(branch1);
        Branch b2 = branches.get(branch2);
        Commit splitPoint = findSplitHelper(branch1, branch2);
        Set<Commit> mergedBranchSet = new HashSet<>();
        for (String branch : b1.getMergedBranches()) {
            Commit mergedSplitCommit = findSplitHelper(branch, branch2);
            if (mergedSplitCommit != null) {
                mergedBranchSet.add(mergedSplitCommit);
            }
        }

        mergedBranchSet.add(splitPoint);
        /* Compare the timestamp of the split point and the merged split point */
        for (Commit commit : mergedBranchSet) {
            if (splitPoint == null || commit.getTimestamp().
                    after(splitPoint.getTimestamp())) {
                splitPoint = commit;
            }
        }
        return splitPoint;
    }

    private Commit findSplitHelper(String branch1, String branch2) {
        Branch b1 = branches.get(branch1);
        Branch b2 = branches.get(branch2);
        if (b1 == null || b2 == null) {
            throw new IllegalArgumentException("Branch does not exist.");
        }

        List<Commit> commits1 = b1.getCommits();
        List<Commit> commits2 = b2.getCommits();

        Set<String> commitIds1 = new HashSet<>();
        for (Commit commit : commits1) {
            commitIds1.add(commit.getHashCode());
        }

        Commit splitPoint = null;
        for (Commit commit : commits2) {
            if (commitIds1.contains(commit.getHashCode())) {
                if (splitPoint == null || commit.getTimestamp().
                        after(splitPoint.getTimestamp())) {
                    splitPoint = commit;
                }
            }
        }
        return splitPoint;
    }

    public void removeBranch(String branchName) {
        branches.remove(branchName);
    }

    public Branch getBranch(String branchName) {
        return branches.get(branchName);
    }

    public Branch getCurrentBranch() {
        return branches.get(currentBranchName);
    }

    public String getCurrentBranchName() {
        return currentBranchName;
    }

    public void setCurrentBranch(String branchName) {
        currentBranchName = branchName;
    }

    // 更新指定分支的头指针
    public void updateBranchHead(String branchName, String commitId) {
        if (!branches.containsKey(branchName)) {
            throw new IllegalArgumentException("A branch with that name does not exist.");
        }
        Branch branch = branches.get(branchName);
        File commitFile = Utils.join(Repository.OBJECTS_DIR, commitId);
        branch.addCommit(Utils.readObject(commitFile, Commit.class));
    }

    public void save() {
        Utils.writeObject(GITLET_FILE, this);
    }

    public static Gitlet load() {
        return Utils.readObject(GITLET_FILE, Gitlet.class);
    }
}
