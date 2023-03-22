package View;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import Chess.ChessBoard;
import Chess.Piece;

import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.util.Map.Entry;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Board extends JPanel {

    private ChessBoard board;
    private int width;
    private int height;

    public Board(ChessBoard board) {
        this.board = board;
        this.width = board.getDimension().width;
        this.height = board.getDimension().height;
        setLayout(new GridLayout(width, height));
    }

    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    @Override
    public void paintComponent(Graphics g) {
        // TODO: Improve this method
        setLayout(new BorderLayout());
        JPanel panelAux = new JPanel();
        panelAux.setLayout(new GridLayout(width, height));
        Box boxes[][] = new Box[width][height];
        Box box;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                box = new Box(j, i);
                if ((i + j) % 2 == 0) {
                    box.setBackground(new Color(227, 206, 167));
                    box.setOpaque(true);
                } else {
                    box.setBackground(new Color(166, 126, 91));
                    box.setOpaque(true);
                }
                boxes[i][j] = box;
                panelAux.add(boxes[i][j]);
            }
        }

        for (Entry<Point, Piece> piece : board.getPieces().entrySet()) {
            boxes[piece.getKey().y][piece.getKey().x].setImagePath(piece.getValue().getImagePath());
        }

        add(panelAux, BorderLayout.CENTER);
    }

    private class Box extends JPanel {

        private BufferedImage image;
        private int x;
        private int y;

        public Box(int x, int y) {
            this.x = x;
            this.y = y;
            try {
                image = ImageIO.read(new File("./assets/none.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent evt) {
                    System.out.println("Clicked on " + x + ", " + y);
                    Board.this.setCursor(Cursor.getDefaultCursor());
                    if (evt.getButton() == MouseEvent.BUTTON3) {
                        setImagePath("./assets/none.png");
                        repaint();
                    }
                }
            });
        }

        public void setImagePath(String imagePath) {
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
