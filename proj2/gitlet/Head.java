package gitlet;

import java.io.*;

public class Head implements Serializable {
    private final String branchName;

    public Head(String branchName) {
        this.branchName = branchName;
    }

    public String getBranchName() {
        return branchName;
    }
}
