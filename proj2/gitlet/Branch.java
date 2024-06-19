package gitlet;

import java.io.Serializable;
import java.util.*;

public class Branch implements Serializable {
    private String name;
    private List<Commit> commits;
    private List<String> mergedBranches;

    public Branch(String name) {
        this.name = name;
        this.commits = new ArrayList<>();
        this.mergedBranches = new ArrayList<>();
    }

    /**
     * Add a commit to the branch.
     * @param commit the commit to be added.
     */
    public void addCommit(Commit commit) {
        commits.add(commit);
    }

    /**
     * Get the last commit of the branch.
     * @return the last commit of the branch.
     */
    public List<Commit> getCommits() {
        return commits;
    }

    /**
     * Get the name of the branch.
     * @return the name of the branch.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the merged branches of the branch.
     * @param branchName the name of the branch to be checked.
     */
    public void addMergedBranch(String branchName) {
        mergedBranches.add(branchName);
    }

    /**
     * Get the merged branches of the branch.
     */
    public List<String> getMergedBranches() {
        return mergedBranches;
    }
}
