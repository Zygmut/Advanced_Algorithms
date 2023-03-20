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
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Map.Entry;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.awt.Point;

public class Board extends JPanel {

    private ChessBoard board;
    private int width;
    private int height;

    public Board(ChessBoard board) {
        this.board = board;
        this.width = board.getDimension().width;
        this.height = board.getDimension().height;
        setLayout(new GridLayout( width, height));
    }

    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    @Override
    public void paintComponent(Graphics g) {
            setLayout(new BorderLayout());
            JPanel panelAux = new JPanel();
            panelAux.setLayout(new GridLayout(width, height));
            Casilla cas[][] = new Casilla[width][height];
            Casilla casilla;
            for (int i = 0; i <  width; i++) {
                for (int j = 0; j < height; j++) {
                    casilla = new Casilla();
                    if ((i + j) % 2 == 0) {
                        casilla.setBackground(new Color(227, 206, 167));
                        casilla.setOpaque(true);
                    } else {
                        casilla.setBackground(new Color(166, 126, 91));
                        casilla.setOpaque(true);
                    }
                    cas[i][j] = casilla;
                    panelAux.add(cas[i][j]);
                }
            }

            for(Entry<Point, Piece> piece : board.getPieces().entrySet()){
                cas[piece.getKey().y][piece.getKey().x].setImagePath(piece.getValue().getImagePath());
            }

            add(panelAux, BorderLayout.CENTER);
    }

    private class Casilla extends JPanel {

        private BufferedImage image;

        public Casilla() {
            try {
                image = ImageIO.read(new File("src/View/none.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        public void setImagePath(String imagePath){
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
