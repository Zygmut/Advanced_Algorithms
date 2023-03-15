package View;

import java.util.Arrays;

import javax.swing.JPanel;

import Model.Piece;

public class Board extends JPanel {

    /**
     * The board is represented by a 2D array of integers. Each integer represents a
     * piece.
     */
    private Piece[][] board;
    /**
     * Default size of the board
     */
    private final int DEFAULT_SIZE = 8;

    /**
     * Default constructor. Creates a board of size 8x8.
     */
    public Board() {
        this.board = new Piece[DEFAULT_SIZE][DEFAULT_SIZE];
    }

    /**
     * Constructor that creates a board of size size x size.
     * 
     * @param size The size of the board.
     */
    public Board(int size) {
        this.board = new Piece[size][size];
    }


    public void initializeBoard() {
        Arrays.fill(this.board, null);
    }

    public void setPiece(int x, int y, Piece piece) {
        this.board[x][y] = piece;
    }

    public Piece getPiece(int x, int y) {
        return this.board[x][y];
    }

    public Piece[][] getBoard() {
        return this.board;
    }

    public void setBoard(Piece[][] board) {
        this.board = board;
    }

}
