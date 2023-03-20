package View;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import Chess.ChessBoard;
import Master.MVC;
import Request.Notify;
import Request.Request;
import Request.RequestCode;
import betterSwing.DirectionAndPosition;
import betterSwing.Section;
import betterSwing.Window;

public class View implements Notify {

    /**
     * The MVC hub of the view.
     */
    private MVC hub;
    /**
     * The window of the view.
     */
    private Window window;
    /**
     * The board of the view.
     */
    private Board board;
    /**
     * The size of the board.
     */
    private int boardSize;
    /**
     * Indicates if the algorithm has started and start a timer.
     */
    private boolean hasStarted;

    /**
     * This constructor creates a view with the MVC hub without any configuration
     *
     * @param mvc The MVC hub of the view.
     * @see MVC
     */
    public View(MVC mvc) {
        this.hub = mvc;
        this.window = new Window();
        this.hasStarted = false;
        this.loadContent();
    }

    /**
     * This constructor creates a view with the MVC hub and configures itself given
     * a config path.
     *
     * @param mvc        The MVC hub of the view.
     * @param configPath The path to its config.
     * @see MVC
     */
    public View(MVC mvc, String configPath) {
        this.hub = mvc;
        this.window = new Window(configPath);
        this.hasStarted = false;
        this.loadContent();
    }

    @Override
    public void notifyRequest(Request request) {
        switch (request.code) {
            case UpdateBoard -> {
                this.updateBoard(this.hub.getModel().getBoard());
            }
            default -> {
                throw new UnsupportedOperationException(
                        request + " is not implemented in " + this.getClass().getSimpleName());
            }
        }
    }

    /**
     * Updates the board of the view.
     * 
     * @param board The new board.
     * @see Board
     */
    private void updateBoard(ChessBoard board) {
        this.board.setBoard(board);
        this.board.paintComponent(this.board.getGraphics());
        this.board.validate();
    }

    /**
     * Loads all the view content.
     * 
     * @see #headerSection()
     * @see #mainSection()
     * @see #sideBarSection()
     * @see #footerSection()
     */
    private void loadContent() {
        this.window.addSection(this.headerSection(), DirectionAndPosition.POSITION_TOP, "Header");
        this.window.addSection(this.mainSection(), DirectionAndPosition.POSITION_CENTER, "MainContent");
        this.window.addSection(this.sideBarSection(), DirectionAndPosition.POSITION_RIGHT, "SideBar");
        this.window.addSection(this.footerSection(), DirectionAndPosition.POSITION_BOTTOM, "Footer");
    }

    /**
     * Creates and returns the header section of the view. This section is mainly
     * used for allowing the user to select the piece to play and the game mode.
     * 
     * @return The header section of the view.
     */
    private Section headerSection() {
        // TODO: Implement this method.
        Section header = new Section();
        JPanel headerContent = new JPanel();
        headerContent.setBackground(Color.RED);
        header.createFreeSection(headerContent);
        return header;
    }

    /**
     * Creates and returns the main section of the view. This section is mainly used
     * for showing the game board.
     * 
     * @return The main section of the view.
     */
    private Section mainSection() {
        Section main = new Section();
        this.board = new Board(this.hub.getModel().getBoard());
        this.board.paintComponent(this.board.getGraphics());
        main.createFreeSection(this.board);
        return main;
    }

    /**
     * Creates and returns the side bar section of the view. This section is mainly
     * used for showing the stats of the game.
     * 
     * @return The side bar section of the view.
     */
    private Section sideBarSection() {
        // TODO: Implement this method.
        Section sideBar = new Section();
        JLabel[] labels = new JLabel[1];
        labels[0] = new JLabel("Estadísticas");
        // Section stats = new Section();
        // stats.createStatsSection(/*TODO: Create this*/);
        sideBar.addLabels(labels, DirectionAndPosition.DIRECTION_ROW);
        return sideBar;
    }

    /**
     * Creates and returns the footer section of the view. This section is mainly
     * used for allowing the user to navigate through different views.
     * 
     * @return The footer section of the view.
     */
    private Section footerSection() {
        // TODO: Implement this method.
        Section footer = new Section();
        JPanel footerContent = new JPanel();
        JLabel tableSize = new JLabel("Tamaño del tablero: ");
        SpinnerNumberModel size = new SpinnerNumberModel(1, 1, 20, 1);
        JSpinner tableSizeSpinner = new JSpinner(size);
        tableSizeSpinner.addChangeListener(e -> {
            this.boardSize = (int) tableSizeSpinner.getValue();
            this.hub.notifyRequest(new Request(RequestCode.ChangedTableSize, this));
        });
        footerContent.add(tableSize);
        footerContent.add(tableSizeSpinner);
        footerContent.setBackground(Color.YELLOW);
        footer.createFreeSection(footerContent);
        return footer;
    }

    /**
     * Returns the window of the view.
     * 
     * @return The window of the view.
     */
    public Window getWindow() {
        return this.window;
    }

}
