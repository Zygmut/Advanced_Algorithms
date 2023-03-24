package utils;

import java.awt.Color;

/**
 * Class that contains all the constants of the project, to avoid magic numbers,
 * strings and make the code more readable and maintainable.
 */
public class Config {

    private Config() {
    }

    /**
     * Path to assets folder.
     */
    public static final String PATH_TO_ASSETS = "./assets/";
    /**
     * Asset name of the piece king.
     */
    public static final String ASSET_NAME_OF_PIECE_KING = "king";
    /**
     * Asset name of the piece queen.
     */
    public static final String ASSET_NAME_OF_PIECE_QUEEN = "queen";
    /**
     * Asset name of the piece rook.
     */
    public static final String ASSET_NAME_OF_PIECE_ROOK = "rook";
    /**
     * Asset name of the piece knight.
     */
    public static final String ASSET_NAME_OF_PIECE_KNIGHT = "knight";
    /**
     * Asset name of the piece bishop.
     */
    public static final String ASSET_NAME_OF_PIECE_BISHOP = "bishop";
    /**
     * Asset name of the piece unicorn.
     */
    public static final String ASSET_NAME_OF_PIECE_UNICORN = "unicorn";
    /**
     * Asset name of the piece dragon.
     */
    public static final String ASSET_NAME_OF_PIECE_DRAGON = "dragon";
    /**
     * Asset name of the piece castle.
     */
    public static final String ASSET_NAME_OF_PIECE_CASTLE = "castle";
    /**
     * Asset name of the piece none.
     */
    public static final String ASSET_NAME_OF_PIECE_NONE = "none";
    /**
     * Asset extension of the pieces.
     */
    public static final String ASSET_EXTENSION_OF_PIECES = ".png";
    /**
     * Path to the window config file.
     */
    public static final String PATH_TO_WINDOW_CONFIG_FILE = "./config.json";
    /**
     * Default board size.
     */
    public static final int INITIAL_DEFAULT_BOARD_SIZE = 8;
    /**
     * Number of different pieces.
     */
    public static final int NUM_OF_DIFFERENT_PIECES = 8;
    /**
     * Default background color of the piece king.
     */
    public static final Color DEFAULT_BACKGROUND_COLOR_KING = Color.RED;
    /**
     * Default background color of the piece queen.
     */
    public static final Color DEFAULT_BACKGROUND_COLOR_QUEEN = Color.BLUE;
    /**
     * Default background color of the piece rook.
     */
    public static final Color DEFAULT_BACKGROUND_COLOR_ROOK = Color.GREEN;
    /**
     * Default background color of the piece knight.
     */
    public static final Color DEFAULT_BACKGROUND_COLOR_KNIGHT = Color.YELLOW;
    /**
     * Default background color of the piece bishop.
     */
    public static final Color DEFAULT_BACKGROUND_COLOR_BISHOP = Color.ORANGE;
    /**
     * Default background color of the piece unicorn.
     */
    public static final Color DEFAULT_BACKGROUND_COLOR_UNICORN = Color.PINK;
    /**
     * Default background color of the piece dragon.
     */
    public static final Color DEFAULT_BACKGROUND_COLOR_DRAGON = Color.CYAN;
    /**
     * Default background color of the piece castle.
     */
    public static final Color DEFAULT_BACKGROUND_COLOR_CASTLE = Color.MAGENTA;
}
