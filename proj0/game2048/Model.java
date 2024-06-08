package game2048;

import java.util.Formatter;
import java.util.Observable;


/** The state of a game of 2048.
 *  @author TODO: YOUR NAME HERE
 */
public class Model extends Observable {
    /** Current contents of the board. */
    private Board board;
    /** Current score. */
    private int score;
    /** Maximum score so far.  Updated when game ends. */
    private int maxScore;
    /** True iff game is ended. */
    private boolean gameOver;

    /* Coordinate System: column C, row R of the board (where row 0,
     * column 0 is the lower-left corner of the board) will correspond
     * to board.tile(c, r).  Be careful! It works like (x, y) coordinates.
     */

    /** Largest piece value. */
    public static final int MAX_PIECE = 2048;

    /** A new 2048 game on a board of size SIZE with no pieces
     *  and score 0. */
    public Model(int size) {
        board = new Board(size);
        score = maxScore = 0;
        gameOver = false;
    }

    /** A new 2048 game where RAWVALUES contain the values of the tiles
     * (0 if null). VALUES is indexed by (row, col) with (0, 0) corresponding
     * to the bottom-left corner. Used for testing purposes. */
    public Model(int[][] rawValues, int score, int maxScore, boolean gameOver) {
        int size = rawValues.length;
        board = new Board(rawValues, score);
        this.score = score;
        this.maxScore = maxScore;
        this.gameOver = gameOver;
    }

    /** Return the current Tile at (COL, ROW), where 0 <= ROW < size(),
     *  0 <= COL < size(). Returns null if there is no tile there.
     *  Used for testing. Should be deprecated and removed.
     *  */
    public Tile tile(int col, int row) {
        return board.tile(col, row);
    }

    /** Return the number of squares on one side of the board.
     *  Used for testing. Should be deprecated and removed. */
    public int size() {
        return board.size();
    }

    /** Return true iff the game is over (there are no moves, or
     *  there is a tile with value 2048 on the board). */
    public boolean gameOver() {
        checkGameOver();
        if (gameOver) {
            maxScore = Math.max(score, maxScore);
        }
        return gameOver;
    }

    /** Return the current score. */
    public int score() {
        return score;
    }

    /** Return the current maximum game score (updated at end of game). */
    public int maxScore() {
        return maxScore;
    }

    /** Clear the board to empty and reset the score. */
    public void clear() {
        score = 0;
        gameOver = false;
        board.clear();
        setChanged();
    }

    /** Add TILE to the board. There must be no Tile currently at the
     *  same position. */
    public void addTile(Tile tile) {
        board.addTile(tile);
        checkGameOver();
        setChanged();
    }

    /** Tilt the board toward SIDE. Return true iff this changes the board.
     *
     * 1. If two Tile objects are adjacent in the direction of motion and have
     *    the same value, they are merged into one Tile of twice the original
     *    value and that new value is added to the score instance variable
     * 2. A tile that is the result of a merge will not merge again on that
     *    tilt. So each move, every tile will only ever be part of at most one
     *    merge (perhaps zero).
     * 3. When three adjacent tiles in the direction of motion have the same
     *    value, then the leading two tiles in the direction of motion merge,
     *    and the trailing tile does not.
     * */
    public boolean tilt(Side side) {
        boolean changed;
        changed = false;

        // TODO: Modify this.board (and perhaps this.score) to account
        // for the tilt to the Side SIDE. If the board changed, set the
        // changed local variable to true.
        board.setViewingPerspective(side);
        int x = board.size();
        for (int i = 0; i < x; i++) {
            int cnt = find_node_number_in_column(i);
            int first_line, second_line, third_line;
            if (cnt == 0) {
                continue;
            } else if (cnt == 1) {
                first_line = find_first_unnull_line(i, 3);
                move_top_node(i, first_line);
                if (first_line != 3) {
                    changed = true;
                }
            } else if (cnt == 2) {
                first_line = find_first_unnull_line(i, 3);
                second_line = find_first_unnull_line(i, first_line - 1);
                if (second_line != 2) {
                    changed = true;
                }
                move_top_node(i, first_line);
                if (board.tile(i, 3).value() == board.tile(i, second_line).value()) {
                    board.move(i, 3, board.tile(i, second_line));
                    score = score + board.tile(i, 3).value();
                    changed = true;
                } else if (second_line != 2) {
                    board.move(i, 2, board.tile(i, second_line));
                    changed = true;
                }
            } else if (cnt == 3) {
                first_line = find_first_unnull_line(i, 3);
                second_line = find_first_unnull_line(i, first_line - 1);
                third_line = find_first_unnull_line(i, second_line - 1);
                move_top_node(i, first_line);
                if (third_line != 3) {
                    changed = true;
                }
                if (board.tile(i, 3).value() == board.tile(i, second_line).value()) {
                    board.move(i, 3, board.tile(i, second_line));
                    score = score + board.tile(i, 3).value();
                    changed = true;
                    board.move(i, 2, board.tile(i, third_line));
                } else if (board.tile(i, third_line).value() == board.tile(i, second_line).value()) {
                    if (second_line != 2) {
                        board.move(i, 2, board.tile(i, 1));
                    }
                    board.move(i, 2, board.tile(i, third_line));
                    changed = true;
                    score += board.tile(i, 2).value();
                } else if (third_line == 0) {
                    if (second_line == 1) {
                        board.move(i, 2, board.tile(i, second_line));
                    }
                    board.move(i, 1, board.tile(i, third_line));
                }
            } else {
                Tile t3 = board.tile(i, 3);
                Tile t2 = board.tile(i, 2);
                Tile t1 = board.tile(i, 1);
                Tile t0 = board.tile(i, 0);
                int k = 0; // k is times of merge
                if (t3.value() == t2.value()) {
                    changed = true;
                    k += 10;
                    board.move(i, 3, t2);
                    score += board.tile(i, 3).value();
                }
                if (t1.value() == t0.value()) {
                    changed = true;
                    k += 1;
                    board.move(i, 1, t0);
                    score += board.tile(i, 1).value();
                }
                if (t3.value() != t2.value() && t2.value() == t1.value()) {
                    changed = true;
                    k += 100;
                    board.move(i, 2, t1);
                    score += board.tile(i, 2).value();
                }
                if (k == 10) { // means only first two merge
                    board.move(i, 2, t1);
                    board.move(i, 1, t0);
                } else if (k == 11) { // means merge twice
                    board.move(i, 2, board.tile(i, 1));
                } else if (k == 100) {
                    board.move(i, 1, t0);
                }
            }
        }
        checkGameOver();
        board.setViewingPerspective(Side.NORTH);
        if (changed) {
            setChanged();
        }
        return changed;
    }
    /*上移最上面的元素*/
    private boolean move_top_node (int i, int first_line){
        if (first_line != 3) {
            board.move(i, 3, board.tile(i, first_line));
            return true;
        }
        return false;
    }
    /*找到最上面不为null的行，如果全为null，返回-1*/
    private int find_first_unnull_line (int i, int j) {
        for (; j >= 0 ; j--) {
            if (board.tile(i, j) != null) {
                return j;
            }
        }
        return -1;
    }
    /*找到此列中元素的数量*/
    private int find_node_number_in_column (int i) {
        int cnt = 0;
        for (int j = 0; j <= 3; j++) {
            if (board.tile(i, j) != null) {
                cnt += 1;
            }
        }
        return cnt;
    }
    /*检查一行里merge过的次数*/
    /** Checks if the game is over and sets the gameOver variable
     *  appropriately.
     */
    private void checkGameOver() {
        gameOver = checkGameOver(board);
    }

    /** Determine whether game is over. */
    private static boolean checkGameOver(Board b) {
        return maxTileExists(b) || !atLeastOneMoveExists(b);
    }

    /** Returns true if at least one space on the Board is empty.
     *  Empty spaces are stored as null.
     * */
    public static boolean emptySpaceExists(Board b) {
        // TODO: Fill in this function.
        int x = b.size();
        for (int i = 0; i < x; i ++) {
            for (int j = 0; j < x; j++) {
                if (b.tile(i, j) == null) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns true if any tile is equal to the maximum valid value.
     * Maximum valid value is given by MAX_PIECE. Note that
     * given a Tile object t, we get its value with t.value().
     */
    public static boolean maxTileExists(Board b) {
        // TODO: Fill in this function.
        int x = b.size();
        for (int i = 0; i < x; i ++) {
            for (int j = 0; j < x; j++) {
                if (b.tile(i, j) != null && b.tile(i, j).value() == MAX_PIECE) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns true if there are any valid moves on the board.
     * There are two ways that there can be valid moves:
     * 1. There is at least one empty space on the board.
     * 2. There are two adjacent tiles with the same value.
     */
    public static boolean atLeastOneMoveExists(Board b) {
        // TODO: Fill in this function.
        int x = b.size();
        int cnt = 0;
        for (int i = 0; i < x; i ++) {
            for (int j = 0; j < x; j++) {
                if (b.tile(i, j) == null) {
                    return true;
                }
            }
        }
        for (int i = 0; i < x; i ++) {
            for (int j = 0; j < x; j++) {
                if (i < 3 && b.tile(i, j).value() == b.tile(i + 1, j).value()) {
                    return true;
                } else if (i > 0 && b.tile(i, j).value() == b.tile(i - 1, j).value()) {
                    return true;
                } else if (j < 3 && b.tile(i, j).value() == b.tile(i, j + 1).value()) {
                    return true;
                } else if (j > 0 && b.tile(i, j).value() == b.tile(i, j - 1).value()) {
                    return true;
                }
            }
        }
        return false;
    }


    @Override
     /** Returns the model as a string, used for debugging. */
    public String toString() {
        Formatter out = new Formatter();
        out.format("%n[%n");
        for (int row = size() - 1; row >= 0; row -= 1) {
            for (int col = 0; col < size(); col += 1) {
                if (tile(col, row) == null) {
                    out.format("|    ");
                } else {
                    out.format("|%4d", tile(col, row).value());
                }
            }
            out.format("|%n");
        }
        String over = gameOver() ? "over" : "not over";
        out.format("] %d (max: %d) (game is %s) %n", score(), maxScore(), over);
        return out.toString();
    }

    @Override
    /** Returns whether two models are equal. */
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (getClass() != o.getClass()) {
            return false;
        } else {
            return toString().equals(o.toString());
        }
    }

    @Override
    /** Returns hash code of Model’s string. */
    public int hashCode() {
        return toString().hashCode();
    }
}
