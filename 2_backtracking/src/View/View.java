package View;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.function.Function;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
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
     * The progress bar of the view. Indicates the progress of the algorithm.
     */
    private JProgressBar progressBar;
    /**
     * The number of pieces of the view. Indicates the number of pieces of the
     * board.
     */
    private int numOfPieces;
    /**
     * Time label of the view. keeps track of the time of the algorithm.
     */
    private JLabel tiempoValue;

    /**
     * This constructor creates a view with the MVC hub without any configuration
     *
     * @param mvc The MVC hub of the view.
     * @see MVC
     */
    public View(MVC mvc) {
        this.hub = mvc;
        this.window = new Window();
        this.numOfPieces = 0;
        this.boardSize = 0;
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
        this.numOfPieces = 0;
        this.boardSize = 0;
        this.loadContent();
    }

    @Override
    public void notifyRequest(Request request) {
        switch (request.code) {
            case UpdateBoard -> {
                this.updateBoard(this.hub.getModel().getBoard());
            }
            default -> {
                System.err.printf("[VIEW]: %s is not implemented.\n", request.toString());
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
        this.progressBar.setValue(getProgressValueToFinish());
        this.board.setBoard(board);
        this.board.paintComponent(this.board.getGraphics());
        this.board.validate();
    }

    private int getProgressValueToFinish() {
        int iteration = this.hub.getModel().getIteration();
        int boardSize = this.hub.getModel().getBoard().size;
        return (int) ((((double) iteration) / ((double) boardSize)) * 100);
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
        this.boardSize = this.hub.getModel().getBoard().getDimension().width; // .height
        this.createProgressBar();
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
        Section header = new Section();
        JPanel headerContent = new JPanel();
        headerContent.setBackground(Color.LIGHT_GRAY);

        final int tamImg = 50;
        final int marginOnX = 5;
        final int marginOnY = 5;

        Function<String, JPanel> addContentToHeader = (String pieceName) -> {
            BufferedImage buffImg = getBufferedImage("./assets/" + pieceName + ".png");
            JLabel label = new JLabel(this.escalateImageIcon(buffImg, tamImg, tamImg));
            label.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
            label.addMouseListener(this.getPieceListener(pieceName, label));
            headerContent.add(label);
            headerContent.add(addMargin(marginOnX, marginOnY));
            return null;
        };

        addContentToHeader.apply("bishop");
        addContentToHeader.apply("dragon");
        addContentToHeader.apply("king");
        addContentToHeader.apply("knight");
        addContentToHeader.apply("pawn");
        addContentToHeader.apply("queen");
        addContentToHeader.apply("rook");
        addContentToHeader.apply("tower");
        addContentToHeader.apply("unicorn");

        header.createFreeSection(headerContent);
        return header;
    }

    private MouseListener getPieceListener(String piece, JLabel label) {
        return new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                // Change cursor icon
                Toolkit toolkit = Toolkit.getDefaultToolkit();
                Image image = toolkit.getImage("./assets/" + piece.toLowerCase() + ".png");
                Point hotSpot = new Point(0, 0);
                Cursor cursor = toolkit.createCustomCursor(image, hotSpot, "Cursor");
                board.setCursor(cursor);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                label.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                label.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                Toolkit toolkit = Toolkit.getDefaultToolkit();
                Image image = toolkit.getImage("./assets/" + piece.toLowerCase() + ".png");
                Point hotSpot = new Point(0, 0);
                Cursor cursor = toolkit.createCustomCursor(image, hotSpot, "Cursor");
                label.setCursor(cursor);
                label.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                label.setCursor(Cursor.getDefaultCursor());
                label.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
            }
        };
    }

    private BufferedImage getBufferedImage(String path) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return img;
    }

    private ImageIcon escalateImageIcon(Image icon, int width, int height) {
        return new ImageIcon(icon.getScaledInstance(width, height, Image.SCALE_SMOOTH));
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
        this.board.setPreferredSize(new Dimension(400, 400));
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
        Section sideBar = new Section();
        JPanel sideBarContent = new JPanel();
        sideBarContent.setBackground(Color.LIGHT_GRAY);
        sideBarContent.setLayout(new BoxLayout(sideBarContent, BoxLayout.Y_AXIS));

        JPanel contentTitleLayout = new JPanel();
        contentTitleLayout.setBackground(Color.LIGHT_GRAY);
        JLabel title = new JLabel("Estadísticas");
        title.setFont(new Font("Arial", Font.ITALIC, 30));
        contentTitleLayout.add(addMargin(10, 10));
        contentTitleLayout.add(title);
        contentTitleLayout.add(addMargin(10, 10));

        sideBarContent.add(addMargin(0, 10));
        sideBarContent.add(contentTitleLayout);

        JPanel infoTam = new JPanel();
        infoTam.setBackground(Color.LIGHT_GRAY);
        JLabel tam = new JLabel("Tamaño del tablero: ");
        tam.setFont(new Font("Arial", Font.ITALIC, 20));
        infoTam.add(tam);
        infoTam.add(addMargin(10, 10));
        JLabel tamValue = new JLabel(this.boardSize + "x" + this.boardSize);
        tamValue.setFont(new Font("Arial", Font.ITALIC, 20));
        infoTam.add(tamValue);
        infoTam.add(addMargin(10, 10));

        sideBarContent.add(addMargin(0, 10));
        sideBarContent.add(infoTam);

        JPanel infoPiezas = new JPanel();
        infoPiezas.setBackground(Color.LIGHT_GRAY);
        JLabel piezas = new JLabel("Piezas en el tablero: ");
        piezas.setFont(new Font("Arial", Font.ITALIC, 20));
        infoPiezas.add(piezas);
        infoPiezas.add(addMargin(10, 10));
        this.numOfPieces = this.hub.getModel().getNumberOfPieces();
        JLabel piezasValue = new JLabel(this.numOfPieces + "");
        piezasValue.setFont(new Font("Arial", Font.ITALIC, 20));
        infoPiezas.add(piezasValue);
        infoPiezas.add(addMargin(10, 10));

        sideBarContent.add(addMargin(0, 10));
        sideBarContent.add(infoPiezas);

        JPanel infoTiempo = new JPanel();
        infoTiempo.setBackground(Color.LIGHT_GRAY);
        JLabel tiempo = new JLabel("Tiempo: ");
        tiempo.setFont(new Font("Arial", Font.ITALIC, 20));
        infoTiempo.add(addMargin(10, 10));
        infoTiempo.add(tiempo);
        tiempoValue = new JLabel("0 ms");
        tiempoValue.setFont(new Font("Arial", Font.ITALIC, 20));
        infoTiempo.add(tiempoValue);
        infoTiempo.add(addMargin(10, 10));

        sideBarContent.add(addMargin(0, 10));
        sideBarContent.add(infoTiempo);

        JPanel infProgresoPanel = new JPanel();
        infProgresoPanel.setBackground(Color.LIGHT_GRAY);
        JLabel progreso = new JLabel("Progreso: ");
        progreso.setFont(new Font("Arial", Font.ITALIC, 20));
        infProgresoPanel.add(addMargin(10, 10));
        infProgresoPanel.add(progreso);
        infProgresoPanel.add(this.progressBar);
        infProgresoPanel.add(addMargin(10, 10));

        sideBarContent.add(addMargin(0, 10));
        sideBarContent.add(infProgresoPanel);

        sideBar.createFreeSection(sideBarContent);
        return sideBar;
    }

    private Component addMargin(int onX, int onY) {
        return Box.createRigidArea(new Dimension(onX, onY));
    }

    private void createProgressBar() {
        this.progressBar = new JProgressBar(0, 100);
        this.progressBar.setValue(0);
        this.progressBar.setForeground(Color.BLACK);
        this.progressBar.setStringPainted(true);
    }

    /**
     * Creates and returns the footer section of the view. This section is mainly
     * used for allowing the user to navigate through different views.
     * 
     * @return The footer section of the view.
     */
    private Section footerSection() {
        Section footer = new Section();

        Section buttonsSection = new Section();
        JButton[] buttons = new JButton[3];
        buttons[0] = new JButton("Iniciar");
        buttons[0].addActionListener(e -> {
            if (buttons[0].getText().equals("Iniciar")) {
                buttons[0].setText("Pausar");
                buttons[2].setEnabled(true);
                this.hub.notifyRequest(new Request(RequestCode.Start, this));
                this.tiempoValue.setText("");
                ImageIcon loading = new ImageIcon("./assets/loading.gif");
                loading.setImage(loading.getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT));
                this.tiempoValue.setIcon(loading);
            } else {
                buttons[2].setEnabled(true);
                String btnText = buttons[0].getText();
                String newBtnText = btnText.equals("Pausar") ? "Reanudar" : "Pausar";
                buttons[0].setText(newBtnText);
                RequestCode code;
                if (btnText.equals("Pausar")) {
                    code = RequestCode.Stop;
                } else {
                    code = RequestCode.Resume;
                }
                this.hub.notifyRequest(new Request(code, this));
            }
        });
        buttons[1] = new JButton("Siguiente iteración");
        buttons[1].addActionListener(e -> {
            buttons[2].setEnabled(true);
            this.hub.notifyRequest(new Request(RequestCode.Next, this));
        });
        buttons[2] = new JButton("Reiniciar");
        buttons[2].setEnabled(false);
        buttons[2].addActionListener(e -> {
            buttons[0].setText("Iniciar");
            buttons[2].setEnabled(false);
            this.tiempoValue.setText("0 ms");
            this.hub.notifyRequest(new Request(RequestCode.ReStart, this));
        });
        buttonsSection.addButtons(buttons, DirectionAndPosition.DIRECTION_ROW);

        JPanel boardSizePanel = new JPanel();
        JLabel tableSize = new JLabel("Tamaño del tablero: ");
        SpinnerNumberModel size = new SpinnerNumberModel(this.boardSize, 1, 20, 1);
        JSpinner tableSizeSpinner = new JSpinner(size);
        tableSizeSpinner.addChangeListener(e -> {
            this.boardSize = (int) tableSizeSpinner.getValue();
            this.hub.notifyRequest(new Request(RequestCode.ChangedTableSize, this));
        });
        boardSizePanel.add(tableSize);
        boardSizePanel.add(tableSizeSpinner);

        JPanel footerPanel = new JPanel();
        footerPanel.add(buttonsSection.getPanel());
        footerPanel.add(boardSizePanel);

        footer.createFreeSection(footerPanel);
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

    public int getBoardSize() {
        return this.boardSize;
    }

}
