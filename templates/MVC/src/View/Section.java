package View;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.util.List;
import java.util.ArrayList;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.MatteBorder;

/**
 * This class represents a section of the application. It can be used to create
 * a histogram chart, a line chart and more. It's basically a JPanel wrapper
 * with extra functionalities that help with the development of the application.
 * Example:
 * 
 * <pre>
 * {@code
 * Section section = new Section();
 * String[] columnLabels = new String[] { "A", "B", "C" };
 * int[] values = new int[] { 1, 2, 3 };
 * Color[] colors = new Color[] { Color.RED, Color.GREEN, Color.BLUE };
 * section.createHistogramChart(columnLabels, values, colors);
 * View v = new View();
 * v.initConfig(null); // "config.txt"
 * v.addSection(section);
 * v.start();
 * </pre>
 */
public class Section {

    private JPanel panel;

    public Section() {
        this.panel = new JPanel();
    }

    /**
     * Creates a histogram chart with the given column labels, values and colors.
     * The Object Section will convert into a HistogramChart Panel.
     * 
     * @param columnLabels The labels of the columns
     * @param values       The values of the columns
     * @param colors       The colors of the columns
     */
    public void createHistogramChart(String[] columnLabels, int[] values, Color[] colors) {
        checkIfArraysAreValid(columnLabels, values, colors);
        HistogramChart chart = new HistogramChart();
        for (int i = 0; i < columnLabels.length; i++) {
            chart.addHistogramColumn(columnLabels[i], values[i], colors[i]);
        }
        chart.layoutHistogram();
        this.panel = chart;
    }

    /**
     * Creates a line chart with the given column labels, values and colors. The
     * Object Section will convert into a LineChart Panel.
     * 
     * @param columnLabels The labels of the columns
     * @param values       The values of the columns
     * @param colors       The colors of the columns
     */
    public void createLineChart(String[] columnLabels, int[] values, Color[] colors) {
        checkIfArraysAreValid(columnLabels, values, colors);
        // TODO: Improve line char drawing
        LineChart chart = new LineChart(values);
        this.panel = chart;
    }

    /**
     * Adds a button to the section. The Object Section will convert into a JPanel 
     * with the button/s in the direction and position specified.
     * 
     * @param buttons         The buttons to add
     * @param positionInPanel The position of the buttons in the panel
     * @param direction       The direction of the buttons in the panel
     */
    public void addButtons(JButton[] buttons, int positionInPanel, int direction) {
        CustomComponent customComponent = new CustomComponent();
        customComponent.addButtons(buttons, positionInPanel, direction);
        this.panel = customComponent;
    }

    private void checkIfArraysAreValid(String[] columnLabels, int[] values, Color[] colors) {
        if (columnLabels.length != values.length || columnLabels.length != colors.length) {
            throw new IllegalArgumentException("The arrays must have the same length");
        }
    }

    public JPanel getPanel() {
        return this.panel;
    }

    private class CustomComponent extends JPanel {
        private static final int DIRECTION_ROW = 0;
        private static final int DIRECTION_COLUMN = 1;
        private static final int POSITION_TOP = 0;
        private static final int POSITION_BOTTOM = 1;
        private static final int POSITION_LEFT = 2;
        private static final int POSITION_RIGHT = 3;
        
        public CustomComponent() {
            setLayout(new BorderLayout());
        } 

        public void addButtons(JButton[] buttons, int positionInPanel, int direction) {
            setDirectionLayout(direction, buttons.length);
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new BorderLayout());
            String alignment = getAlignment(positionInPanel);
            for (JButton jButton : buttons) {
                buttonPanel.add(jButton, alignment);
            }
        }

        private void setDirectionLayout(int direction, int length) {
            switch (direction) {
                case DIRECTION_ROW -> setLayout(new GridLayout(1, length));
                case DIRECTION_COLUMN -> setLayout(new GridLayout(length, 1));
                default -> throw new IllegalArgumentException("Invalid direction");
            }
        }

        private String getAlignment(int positionInPanel) {
            switch (positionInPanel) {
                case POSITION_TOP -> {
                    return BorderLayout.NORTH;
                }
                case POSITION_BOTTOM-> {
                    return BorderLayout.SOUTH;
                }
                case POSITION_LEFT -> {
                    return BorderLayout.WEST;
                }
                case POSITION_RIGHT -> {
                    return BorderLayout.EAST;
                }
                default -> throw new IllegalArgumentException("Invalid position");
            }
        }
    }

    private class LineChart extends JPanel {
        private int[] yCoords;
        private int startX = 100;
        private int startY = 100;
        private int endX = 400;
        private int endY = 400;
        private int unitX = (endX - startX) / 10;
        private int unitY = (endY - startY) / 10;
        private int prevX = startX;
        private int prevY = endY;

        public LineChart(int[] yCoords) {
            this.yCoords = yCoords;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            // We draw in the following 2 loops the grid so it's visible what I explained
            // before about each "unit"
            g2d.setColor(Color.BLUE);
            for (int i = startX; i <= endX; i += unitX) {
                g2d.drawLine(i, startY, i, endY);
            }

            for (int i = startY; i <= endY; i += unitY) {
                g2d.drawLine(startX, i, endX, i);
            }

            // We draw the axis here instead of before because otherwise they would become
            // blue colored.
            g2d.setColor(Color.BLACK);
            g2d.drawLine(startX, startY, startX, endY);
            g2d.drawLine(startX, endY, endX, endY);

            // We draw each of our coords in red color
            g2d.setColor(Color.RED);
            for (int y : yCoords) {
                g2d.drawLine(prevX, prevY, prevX += unitX, prevY = endY - (y * unitY));
            }
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(endX + 100, endY + 100);
        }
    }

    private class HistogramChart extends JPanel {
        private int histogramHeight = 200;
        private int barWidth = 50;
        private int barGap = 10;

        private JPanel barPanel;
        private JPanel labelPanel;

        private List<Bar> bars = new ArrayList<Bar>();

        public HistogramChart() {
            setBorder(new EmptyBorder(10, 10, 10, 10));
            setLayout(new BorderLayout());

            barPanel = new JPanel(new GridLayout(1, 0, barGap, 0));
            Border outer = new MatteBorder(1, 1, 1, 1, Color.BLACK);
            Border inner = new EmptyBorder(10, 10, 0, 10);
            Border compound = new CompoundBorder(outer, inner);
            barPanel.setBorder(compound);

            labelPanel = new JPanel(new GridLayout(1, 0, barGap, 0));
            labelPanel.setBorder(new EmptyBorder(5, 10, 0, 10));

            add(barPanel, BorderLayout.CENTER);
            add(labelPanel, BorderLayout.PAGE_END);
        }

        public void addHistogramColumn(String label, int value, Color color) {
            Bar bar = new Bar(label, value, color);
            bars.add(bar);
        }

        public void layoutHistogram() {
            barPanel.removeAll();
            labelPanel.removeAll();

            int maxValue = 0;

            for (Bar bar : bars)
                maxValue = Math.max(maxValue, bar.getValue());

            for (Bar bar : bars) {
                JLabel label = new JLabel(bar.getValue() + "");
                label.setHorizontalTextPosition(JLabel.CENTER);
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setVerticalTextPosition(JLabel.TOP);
                label.setVerticalAlignment(JLabel.BOTTOM);
                int barHeight = (bar.getValue() * histogramHeight) / maxValue;
                Icon icon = new ColorIcon(bar.getColor(), barWidth, barHeight);
                label.setIcon(icon);
                barPanel.add(label);

                JLabel barLabel = new JLabel(bar.getLabel());
                barLabel.setHorizontalAlignment(JLabel.CENTER);
                labelPanel.add(barLabel);
            }
        }
    }

    private class ColorIcon implements Icon {
        private int shadow = 3;

        private Color color;
        private int width;
        private int height;

        public ColorIcon(Color color, int width, int height) {
            this.color = color;
            this.width = width;
            this.height = height;
        }

        public int getIconWidth() {
            return width;
        }

        public int getIconHeight() {
            return height;
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            g.setColor(color);
            g.fillRect(x, y, width - shadow, height);
            g.setColor(Color.GRAY);
            g.fillRect(x + width - shadow, y + shadow, shadow, height - shadow);
        }
    }

    private class Bar {
        private String label;
        private int value;
        private Color color;

        public Bar(String label, int value, Color color) {
            this.label = label;
            this.value = value;
            this.color = color;
        }

        public String getLabel() {
            return label;
        }

        public int getValue() {
            return value;
        }

        public Color getColor() {
            return color;
        }
    }
}
