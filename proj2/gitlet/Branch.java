package gitlet;

import java.io.Serializable;
import java.util.*;

public class Branch implements Serializable {
    private String name;
    private List<Commit> commits;

    public Branch(String name) {
        this.name = name;
        this.commits = new ArrayList<>();
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
}