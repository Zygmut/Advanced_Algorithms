package Model;

import java.awt.Point;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Board implements Serializable {

    int[][] state;
    Point dudPosition;
    boolean verbose;

    public Board(int size) {
        this.state = generateFinalState(size);
        dudPosition = new Point(size - 1, size - 1);
        this.verbose = false;
    }

    // Clone Constructor (java:S2975)
    public Board(Board clone) {
        this.state = new int[clone.getState().length][];
        for (int i = 0; i < state.length; i++) {
            this.state[i] = Arrays.copyOf(clone.getState()[i], clone.getState()[i].length);
        }
        this.dudPosition = new Point(clone.getDudPosition());
        this.verbose = clone.isVerbose();
    }

    private int[][] generateFinalState(int size) {

        int[][] board = new int[size][size];
        int value = 1;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = value;
                value++;
            }
        }

        // Set the bottom-right cell as the empty cell (represented by 0)
        board[size - 1][size - 1] = -1;

        return board;

    }

    public boolean move(Movement movement) {
        boolean success = switch (movement) {
            case UP -> swapDud(-1, 0);
            case DOWN -> swapDud(1, 0);
            case LEFT -> swapDud(0, -1);
            case RIGHT -> swapDud(0, 1);
        };

        if (verbose) {
            if (success) {
                Logger.getLogger(this.getClass().getSimpleName())
                        .log(Level.INFO, "Moved {0}", movement);
            } else {
                Logger.getLogger(this.getClass().getSimpleName())
                        .log(Level.WARNING, "Tried moving {0}, but failed", movement);
            }
        }

        return success;
    }

    private boolean swapDud(int targetRowDisplacement, int targetColDisplacement) {
        final int dudX = dudPosition.x;
        final int dudY = dudPosition.y;

        if (!inBounds(dudX + targetColDisplacement, dudY + targetRowDisplacement)) {
            return false;
        }

        this.state[dudY][dudX] = this.state[dudY + targetRowDisplacement][dudX + targetColDisplacement];
        this.state[dudY + targetRowDisplacement][dudX + targetColDisplacement] = -1;

        dudPosition.translate(targetColDisplacement, targetRowDisplacement);

        return true;
    }

    public void shuffle(int nSwaps, int seed) {
        Random rng = new Random(seed);

        for (int i = 0; i < nSwaps; i++) {
            while (!this.move(Movement.values()[rng.nextInt(Movement.values().length)]))
                ;
        }
    }

    private boolean inBounds(int x, int y) {
        return x >= 0 && x < this.state.length && y >= 0 && y < this.state.length;
    }

    public boolean isSolved() {
        return Arrays.deepEquals(this.state, this.generateFinalState(this.state.length));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int[] row : state) {
            for (int value : row) {
                sb.append(value).append("\t");
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    public int[][] getState() {
        return state;
    }

    public Point getDudPosition() {
        return dudPosition;
    }

    public boolean isVerbose() {
        return this.verbose;
    }

    public void setVerbosity(boolean verbosity) {
        this.verbose = verbosity;
    }

}
