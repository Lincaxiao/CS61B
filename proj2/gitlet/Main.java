package gitlet;

import java.io.File;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Zhang, Xiaochen
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        /* If the args[] is empty */
        if (args.length == 0) {
            Utils.message("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                checkOperands(args.length, 1);
                Repository.gitInit();
                break;
            case "add":
                checkOperands(args.length, 2);
                checkGitdirectory();
                Repository.gitAdd(args[1]);
                break;
            case "commit":
                checkOperands(args.length, 2);
                Repository.gitCommit(args[1]);
                break;
            case "rm":
                checkGitdirectory();
                checkOperands(args.length, 2);
                Repository.gitRm(args[1]);
                break;
            case "log":
                checkGitdirectory();
                checkOperands(args.length, 1);
                Repository.gitLog();
                break;
            case "global-log":
                checkGitdirectory();
                checkOperands(args.length, 1);
                Repository.gitGloballog();
                break;
            case "find":
                checkGitdirectory();;
                checkOperands(args.length, 2);
                Repository.gitFind(args[1]);
                break;
            case "status":
                checkGitdirectory();
                checkOperands(args.length, 1);
                Repository.gitStatus();
                break;
            case "checkout":
                checkGitdirectory();
                Repository.gitCheckout(args);
                break;
            case "branch":
                checkGitdirectory();
                checkOperands(args.length, 2);
                Repository.gitBranch(args[1]);
                break;
            case "rm-branch":
                checkGitdirectory();
                checkOperands(args.length, 2);
                Repository.gitRmbranch(args[1]);
                break;
            case "reset":
                checkGitdirectory();
                checkOperands(args.length, 2);
                Repository.gitReset(args[1]);
                break;
            case "merge":
                checkGitdirectory();
                checkOperands(args.length, 2);
                Repository.gitMerge(args[1]);
                break;
            case "add-remote":
                checkGitdirectory();
                checkOperands(args.length, 3);
                Repository.gitAddremote(args[1], args[2]);
                break;
            case "rm-remote":
                checkGitdirectory();
                checkOperands(args.length, 2);
                Repository.gitRmremote(args[1]);
                break;
            case "push":
                checkGitdirectory();
                checkOperands(args.length, 3);
                Repository.gitPush(args[1], args[2]);
                break;
            default:
                checkGitdirectory();
                Utils.message("No command with that name exists.");
                System.exit(0);
        }
    }

    /**
     * Check if the current directory is a Gitlet directory.
     */
    private static void checkGitdirectory() {
        File f = Utils.join(Repository.CWD, ".gitlet");
        if (!f.exists()) {
            Utils.message("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }

    /**
     * Check if the number of operands is correct.
     * @param arrayNum
     * @param num
     */
    private static void checkOperands (int arrayNum, int num) {
        if (arrayNum != num) {
            Utils.message("Incorrect operands.");
            System.exit(0);
        }
    }
}
