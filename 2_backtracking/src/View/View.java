package View;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import java.awt.GridLayout;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.util.Map.Entry;
import java.awt.Graphics2D;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import betterSwing.utils.DirectionAndPosition;
import betterSwing.Section;
import betterSwing.Window;
import Chess.ChessBoard;
import Chess.Piece;
import Chess.Pieces;
import Master.MVC;
import Request.Notify;
import Request.Request;
import Request.RequestCode;
import utils.Config;
import utils.Helpers;

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
    private int boardWidth;
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
     * The last piece string of the view. Keeps track of the last piece string.
     */
    private String lastPieceString;
    /**
     * The last piece of the view. Keeps track of the last piece.
     */
    private Piece lastPiece;
    /**
     * The last point of the view. Keeps track of the last point.
     */
    private Point lastPoint;
    /**
     * The label of the size of the board.
     */
    private JLabel tamValue;
    /**
     * The label of the number of pieces on the board.
     */
    private JLabel piezasValue;

    /**
     * This constructor creates a view with the MVC hub without any configuration
     *
     * @param mvc The MVC hub of the view.
     * @see MVC
     */
    public View(MVC mvc) {
        this.hub = mvc;
        this.window = new Window();
        this.window.initConfig();
        this.numOfPieces = 0;
        this.boardWidth = 0;
        this.lastPieceString = null;
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
        this.window.initConfig();
        this.numOfPieces = 0;
        this.boardWidth = 0;
        this.lastPieceString = null;
        this.loadContent();
    }

    @Override
    public void notifyRequest(Request request) {
        switch (request.code) {
            case UPDATEDBOARD -> {
                this.updateBoard(this.hub.getModel().getBoard());
            }
            case CHANGEDTABLESIZE -> {
                this.tamValue.setText(this.boardWidth + "x" + this.boardWidth);
                this.board.removeAll();
                this.updateBoard(this.hub.getModel().getBoard());
            }
            case HASFINISHED -> {
                this.tiempoValue.setIcon(null);
                this.tiempoValue.setText(this.hub.getModel().getElapsedTime() + " ms");
            }
            default -> {
                Logger.getLogger(this.getClass().getSimpleName())
                        .log(Level.SEVERE, "{0} is not implemented.", request);
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
        this.boardWidth = this.hub.getModel().getBoard().width; // .height
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
            BufferedImage buffImg = getBufferedImage(
                    Config.PATH_TO_ASSETS + pieceName + Config.ASSET_EXTENSION_OF_PIECES);
            JLabel label = new JLabel(this.escalateImageIcon(buffImg, tamImg, tamImg));
            label.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
            label.addMouseListener(this.getPieceListener(pieceName, label));
            headerContent.add(label);
            headerContent.add(addMargin(marginOnX, marginOnY));
            return null;
        };

        addContentToHeader.apply(Config.ASSET_NAME_OF_PIECE_KING);
        addContentToHeader.apply(Config.ASSET_NAME_OF_PIECE_QUEEN);
        addContentToHeader.apply(Config.ASSET_NAME_OF_PIECE_ROOK);
        addContentToHeader.apply(Config.ASSET_NAME_OF_PIECE_KNIGHT);
        addContentToHeader.apply(Config.ASSET_NAME_OF_PIECE_BISHOP);
        addContentToHeader.apply(Config.ASSET_NAME_OF_PIECE_UNICORN);
        addContentToHeader.apply(Config.ASSET_NAME_OF_PIECE_DRAGON);
        addContentToHeader.apply(Config.ASSET_NAME_OF_PIECE_CASTLE);

        header.createFreeSection(headerContent);
        return header;
    }

    private MouseListener getPieceListener(String piece, JLabel label) {
        return new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                // Change cursor icon
                Toolkit toolkit = Toolkit.getDefaultToolkit();
                Image image = toolkit.getImage(Helpers.getAssetPath(piece));
                Point hotSpot = new Point(0, 0);
                Cursor cursor = toolkit.createCustomCursor(image, hotSpot, "Cursor");
                board.setCursor(cursor);
                lastPieceString = piece;
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
                Image image = toolkit.getImage(Helpers.getAssetPath(piece));
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
        final String font = "Arial";
        Section sideBar = new Section();
        JPanel sideBarContent = new JPanel();
        sideBarContent.setBackground(Color.LIGHT_GRAY);
        sideBarContent.setLayout(new BoxLayout(sideBarContent, BoxLayout.Y_AXIS));

        JPanel contentTitleLayout = new JPanel();
        contentTitleLayout.setBackground(Color.LIGHT_GRAY);
        JLabel title = new JLabel("Estadísticas");
        title.setFont(new Font(font, Font.ITALIC, 30));
        contentTitleLayout.add(addMargin(10, 10));
        contentTitleLayout.add(title);
        contentTitleLayout.add(addMargin(10, 10));

        sideBarContent.add(addMargin(0, 10));
        sideBarContent.add(contentTitleLayout);

        JPanel infoTam = new JPanel();
        infoTam.setBackground(Color.LIGHT_GRAY);
        JLabel tam = new JLabel("Tamaño del tablero: ");
        tam.setFont(new Font(font, Font.ITALIC, 20));
        infoTam.add(tam);
        infoTam.add(addMargin(10, 10));
        this.tamValue = new JLabel(this.boardWidth + "x" + this.boardWidth);
        this.tamValue.setFont(new Font(font, Font.ITALIC, 20));
        infoTam.add(this.tamValue);
        infoTam.add(addMargin(10, 10));

        sideBarContent.add(addMargin(0, 10));
        sideBarContent.add(infoTam);

        JPanel infoPiezas = new JPanel();
        infoPiezas.setBackground(Color.LIGHT_GRAY);
        JLabel piezas = new JLabel("Piezas en el tablero: ");
        piezas.setFont(new Font(font, Font.ITALIC, 20));
        infoPiezas.add(piezas);
        infoPiezas.add(addMargin(10, 10));
        this.numOfPieces = this.hub.getModel().getNumberOfPieces();
        this.piezasValue = new JLabel(this.numOfPieces + "");
        this.piezasValue.setFont(new Font(font, Font.ITALIC, 20));
        infoPiezas.add(this.piezasValue);
        infoPiezas.add(addMargin(10, 10));

        sideBarContent.add(addMargin(0, 10));
        sideBarContent.add(infoPiezas);

        JPanel infoTiempo = new JPanel();
        infoTiempo.setBackground(Color.LIGHT_GRAY);
        JLabel tiempo = new JLabel("Tiempo: ");
        tiempo.setFont(new Font(font, Font.ITALIC, 20));
        infoTiempo.add(addMargin(10, 10));
        infoTiempo.add(tiempo);
        tiempoValue = new JLabel("0 ms");
        tiempoValue.setFont(new Font(font, Font.ITALIC, 20));
        infoTiempo.add(tiempoValue);
        infoTiempo.add(addMargin(10, 10));

        sideBarContent.add(addMargin(0, 10));
        sideBarContent.add(infoTiempo);

        JPanel infProgresoPanel = new JPanel();
        infProgresoPanel.setBackground(Color.LIGHT_GRAY);
        JLabel progreso = new JLabel("Progreso: ");
        progreso.setFont(new Font(font, Font.ITALIC, 20));
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
        final String Startstr = "Iniciar";
        final String Endstr = "Pausar";
        buttons[0] = new JButton(Startstr);
        buttons[0].addActionListener(e -> {
            if (buttons[0].getText().equals(Startstr)) {
                buttons[0].setText(Endstr);
                buttons[2].setEnabled(true);
                this.hub.notifyRequest(new Request(RequestCode.START, this));
                this.tiempoValue.setText("");
                ImageIcon loading = new ImageIcon("./assets/loading.gif");
                loading.setImage(loading.getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT));
                this.tiempoValue.setIcon(loading);
            } else {
                buttons[2].setEnabled(true);
                String btnText = buttons[0].getText();
                String newBtnText = btnText.equals(Endstr) ? "Reanudar" : Endstr;
                buttons[0].setText(newBtnText);
                RequestCode code;
                if (btnText.equals(Endstr)) {
                    code = RequestCode.STOP;
                } else {
                    code = RequestCode.RESUME;
                }
                this.hub.notifyRequest(new Request(code, this));
            }
        });
        buttons[1] = new JButton("Siguiente iteración");
        buttons[1].addActionListener(e -> {
            buttons[2].setEnabled(true);
            this.hub.notifyRequest(new Request(RequestCode.NEXT, this));
        });
        buttons[2] = new JButton("Reiniciar");
        buttons[2].setEnabled(false);
        buttons[2].addActionListener(e -> {
            buttons[0].setText(Startstr);
            buttons[2].setEnabled(false);
            this.tiempoValue.setText("0 ms");
            this.hub.notifyRequest(new Request(RequestCode.RESTART, this));
        });
        buttonsSection.createButtons(buttons, DirectionAndPosition.DIRECTION_ROW);

        JPanel boardSizePanel = new JPanel();
        JLabel tableSize = new JLabel("Tamaño del tablero: ");
        SpinnerNumberModel size = new SpinnerNumberModel(this.boardWidth, 1, 32, 1);
        JSpinner tableSizeSpinner = new JSpinner(size);
        tableSizeSpinner.addChangeListener(e -> {
            this.boardWidth = (int) tableSizeSpinner.getValue();
            this.hub.notifyRequest(new Request(RequestCode.CHANGEDTABLESIZE, this));
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

    public String getLastPieceString() {
        return this.lastPieceString;
    }

    public Piece getLastPiece() {
        return this.lastPiece;
    }

    public Point getLastPoint() {
        return this.lastPoint;
    }

    public int getBoardWidth() {
        return boardWidth;
    }

    /**
     * Draws an arrow between two points in a graphics context.
     *
     * @param g          The graphics context to draw the arrow in.
     * @param x0         The x coordinate of the start point.
     * @param y0         The y coordinate of the start point.
     * @param x1         The x coordinate of the end point.
     * @param y1         The y coordinate of the end point.
     * @param headLength The length of the arrow head.
     * @param headAngle  The angle of the arrow head.
     */
    private void drawArrow(Graphics g, int x0, int y0, int x1, int y1, int headLength, int headAngle) {
        final float lineWidth = 1.5f;
        final float headWidth = 2f;
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        double offs = headAngle * Math.PI / 180.0;
        double angle = Math.atan2(y0 - y1, x0 - x1);
        int[] xs = { x1 + (int) (headLength * Math.cos(angle + offs)), x1,
                x1 + (int) (headLength * Math.cos(angle - offs)) };
        int[] ys = { y1 + (int) (headLength * Math.sin(angle + offs)), y1,
                y1 + (int) (headLength * Math.sin(angle - offs)) };
        g2.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND));
        g2.drawLine(x0, y0, x1, y1);
        g2.setStroke(new BasicStroke(headWidth, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND));
        g2.drawPolyline(xs, ys, 3);
    }

    /**
     * This class represents a section of the view. Particularlly, it's a chess
     * board for the game. Each box of the board is made with the class BoxBoard.
     *
     * @see BoxBoard
     */
    public class Board extends JPanel {

        private ChessBoard content;

        public Board(ChessBoard board) {
            this.content = board;
            setLayout(new GridLayout(board.width, board.height));
        }

        public void setBoard(ChessBoard board) {
            this.content = board;
        }

        @Override
        public void paintComponent(Graphics g) {
            setLayout(new BorderLayout());
            JPanel panelAux = new JPanel();
            panelAux.setLayout(new GridLayout(content.width, content.height));
            BoxBoard[][] boxes = new BoxBoard[content.width][content.height];
            BoxBoard box;
            Color color;
            for (int i = 0; i < content.width; i++) {
                for (int j = 0; j < content.height; j++) {
                    box = new BoxBoard(j, i);
                    if ((i + j) % 2 == 0) {
                        color = new Color(227, 206, 167);
                        box.setBackground(color);
                        box.setColor(color);
                        box.setOpaque(true);
                    } else {
                        color = new Color(166, 126, 91);
                        box.setBackground(color);
                        box.setColor(color);
                        box.setOpaque(true);
                    }
                    box.calcCenterPoint();
                    boxes[i][j] = box;
                    panelAux.add(boxes[i][j]);
                }
            }

            for (Entry<Point, Piece> piece : content.getPieces()) {
                boxes[piece.getKey().y][piece.getKey().x].setImagePath(piece.getValue().getImagePath());
            }

            add(panelAux, BorderLayout.CENTER);
        }

        private class BoxBoard extends JPanel {

            private BufferedImage image;
            private int xPos, yPos, centerX, centerY;
            private Color color;

            public BoxBoard(int x, int y) {
                this.xPos = x;
                this.yPos = y;
                this.centerX = 0;
                this.centerY = 0;
                try {
                    image = ImageIO.read(new File(Helpers.getAssetPath(Config.ASSET_NAME_OF_PIECE_NONE)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                this.addMouseListener(this.createMouseListner());
            }

            private void calcCenterPoint() {
                this.centerX = this.getWidth() / 2;
                this.centerY = this.getHeight() / 2;
            }

            private int[] getCenterPoint() {
                return new int[] { this.centerX, this.centerY };
            }

            private Piece getLastPiece(String imagePath) {
                return switch (imagePath) {
                    case Config.ASSET_NAME_OF_PIECE_BISHOP -> Pieces.BISHOP;
                    case Config.ASSET_NAME_OF_PIECE_DRAGON -> Pieces.DRAGON;
                    case Config.ASSET_NAME_OF_PIECE_KING -> Pieces.KING;
                    case Config.ASSET_NAME_OF_PIECE_KNIGHT -> Pieces.KNIGHT;
                    case Config.ASSET_NAME_OF_PIECE_QUEEN -> Pieces.QUEEN;
                    case Config.ASSET_NAME_OF_PIECE_CASTLE -> Pieces.CASTLE;
                    case Config.ASSET_NAME_OF_PIECE_ROOK -> Pieces.ROOK;
                    case Config.ASSET_NAME_OF_PIECE_UNICORN -> Pieces.UNICORN;
                    default -> null;
                };
            }

            private MouseListener createMouseListner() {
                return new MouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent evt) {
                        Board.this.setCursor(Cursor.getDefaultCursor());
                        if (evt.getButton() == MouseEvent.BUTTON3) {
                            View.this.lastPoint = new Point(xPos, yPos);
                            View.this.hub.notifyRequest(new Request(RequestCode.DELETEDPIECE, View.this));
                            View.this.numOfPieces = View.this.hub.getModel().getNumberOfPieces();
                            View.this.piezasValue.setText(View.this.numOfPieces + "");
                            View.this.lastPoint = null;
                            setImagePath(Helpers.getAssetPath(Config.ASSET_NAME_OF_PIECE_NONE));
                            repaint();
                        }
                        if (evt.getButton() == MouseEvent.BUTTON1) {
                            String imageName = View.this.lastPieceString;
                            if (imageName != null) {
                                View.this.lastPiece = getLastPiece(imageName);
                                View.this.lastPoint = new Point(xPos, yPos);
                                View.this.hub.notifyRequest(new Request(RequestCode.CHANGEDPIECE, View.this));
                                View.this.numOfPieces = View.this.hub.getModel().getNumberOfPieces();
                                View.this.piezasValue.setText(View.this.numOfPieces + "");
                                setImagePath(Helpers.getAssetPath(imageName));
                                repaint();
                                View.this.lastPieceString = null;
                                View.this.lastPiece = null;
                                View.this.lastPoint = null;
                            }
                        }
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                        // Do nothing
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        // Do nothing
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        BoxBoard.this.setBackground(Color.LIGHT_GRAY);
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        BoxBoard.this.setBackground(color);
                    }
                };
            }

            private void setColor(Color color) {
                this.color = color;
            }

            private void setImagePath(String imagePath) {
                try {
                    image = ImageIO.read(new File(imagePath));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.drawImage(image, 0, 0, getWidth(), getHeight(), null);
            }

        }
    }

}
