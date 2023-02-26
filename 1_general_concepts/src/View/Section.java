package View;

import java.util.List;
import java.util.ArrayList;
import java.awt.BasicStroke;
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
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

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
        checkIfArraysAreValid(columnLabels.length, values.length, colors.length);
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
     * @param labels The labels of the columns
     * @param values The values of the columns
     * @param colors The colors of the columns
     */
    public void createLineChart(String[] labels, int[][] values, Color[] colors, String[] lineNames) {
        assert labels.length == 2; // X e Y
        checkIfArraysAreValid(lineNames.length, values.length, colors.length);
        MultiLineChart chart = new MultiLineChart(values, colors);
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
    public void addButtons(JButton[] buttons, int direction) {
        CustomComponent customComponent = new CustomComponent();
        customComponent.addButtons(buttons, direction);
        this.panel = customComponent;
    }

    /**
     * Adds a label to the section. The Object Section will convert into a JPanel
     * with the button/s in the direction and position specified.
     *
     * @param labels          The labels to add
     * @param positionInPanel The position of the buttons in the panel
     * @param direction       The direction of the buttons in the panel
     */
    public void addLabels(JLabel[] labels, int direction) {
        CustomComponent customComponent = new CustomComponent();
        customComponent.addLabels(labels, direction);
        this.panel = customComponent;
    }

    /**
     * Adds a progress bar to the section. The Object Section will convert into a
     * JPanel with the button/s in the direction and position specified.
     *
     * @param progressBars    The progress bars to add
     * @param positionInPanel The position of the buttons in the panel
     * @param direction       The direction of the buttons in the panel
     */
    public void addProgressBar(JProgressBar[] progressBars, int direction) {
        CustomComponent customComponent = new CustomComponent();
        customComponent.addProgressBar(progressBars, direction);
        this.panel = customComponent;
    }

    /**
     * Adds a legend to the section. The Object Section will convert into a JPanel
     * with the legend in the direction and position specified. The length of the 
     * labels and colors arrays must be the same. If the iconSize is -1, the icon 
     * size will be the default (10).
     *
     * @param labels          The labels of the legend
     * @param colors          The colors of the legend
     * @param positionInPanel The position of the legend in the panel
     * @param direction       The direction of the legend in the panel
     * @param iconSize        The size of the icon in the legend
     */
    public void addLegend(String[] labels, Color[] colors, int direction, int iconSize) {
        checkIfArraysAreValid(labels.length, colors.length);
        Legend legend = new Legend();
        legend.addLegend(labels, colors, direction, iconSize);
        this.panel = legend;
    }

    private void checkIfArraysAreValid(int... lengths) {
        for (int i = 0; i < lengths.length - 1; i++) {
            if (lengths[i] != lengths[i + 1]) {
                throw new IllegalArgumentException("The arrays must have the same length, one or more parameters are not the same length");
            }
        }
    }

    public JPanel getPanel() {
        return this.panel;
    }

    private class Legend extends JPanel {

        public Legend() {
            super();
        }

        public void addLegend(String[] labels, Color[] colors, int direction, int iconSize) {
            checkIconSize(iconSize);
            setLayout(DirectionAndPosition.setDirectionLayout(direction, labels.length));
            for (int i = 0; i < labels.length; i++) {
                JLabel label = new JLabel(labels[i]);
                if (iconSize != -1) {
                    label.setIcon(new ColorIcon(colors[i], iconSize, iconSize));
                } else {
                    label.setIcon(new ColorIcon(colors[i]));
                }
                add(label);
            }
        }

        private void checkIconSize(int iconSize) {
            if (iconSize == -1) {
                // Default icon size
                return;
            }
            if (iconSize < 0) {
                throw new IllegalArgumentException("The icon size must be greater than 0");
            }
        }
    }

    public class MultiLineChart extends JPanel {

        private int[][] data;
        private Color[] colors;
        private int rows;
        private int columns;
        private int padding = 25;

        public MultiLineChart(int[][] data, Color[] colors) {
            this.data = data;
            this.colors = colors;
            this.rows = data.length;
            this.columns = data[0].length;
            setPreferredSize(new Dimension(400, 300));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            int width = getWidth();
            int height = getHeight();
            int chartWidth = width - 2 * padding;
            int chartHeight = height - 2 * padding;

            // draw grid lines
            g2d.setColor(Color.LIGHT_GRAY);
            for (int i = 1; i < columns; i++) {
                int x = padding + chartWidth * i / columns;
                g2d.drawLine(x, padding, x, padding + chartHeight);
            }
            for (int i = 1; i < rows; i++) {
                int y = padding + chartHeight * i / rows;
                g2d.drawLine(padding, y, padding + chartWidth, y);
            }

            // draw lines and circles
            for (int i = 0; i < rows; i++) {
                g2d.setColor(colors[i]);
                int x1 = padding;
                int y1 = height - padding - data[i][0] * chartHeight / 100;
                for (int j = 1; j < columns; j++) {
                    int x2 = padding + chartWidth * j / columns;
                    int y2 = height - padding - data[i][j] * chartHeight / 100;
                    g2d.drawLine(x1, y1, x2, y2);
                    g2d.fillOval(x2 - 3, y2 - 3, 6, 6);
                    x1 = x2;
                    y1 = y2;
                }
            }

            // draw x and y axis
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine(padding, height - padding, width - padding, height - padding);
            g2d.drawLine(padding, padding, padding, height - padding);

            // draw numbers for x and y axis
            g2d.setStroke(new BasicStroke(1));
            for (int i = 0; i <= columns; i++) {
                int x = padding + chartWidth * i / columns;
                g2d.drawString(Integer.toString(i * 10), x - 5, height - padding + 15);
            }
            for (int i = 0; i <= rows; i++) {
                int y = padding + chartHeight * i / rows;
                g2d.drawString(Integer.toString(100 - i * 10), 5, y + 5);
            }
        }
    }

    /**
     * This class represents the possible direction and position values of the
     * components in the view and section.
     */
    public class DirectionAndPosition {
        public static final int DIRECTION_ROW = 0;
        public static final int DIRECTION_COLUMN = 1;
        public static final int POSITION_TOP = 0;
        public static final int POSITION_BOTTOM = 1;
        public static final int POSITION_LEFT = 2;
        public static final int POSITION_RIGHT = 3;
        public static final int POSITION_CENTER = 4;

        /**
         * Returns the direction of the components in the panel.
         * 
         * @param position The position of the components in the panel
         * @return The direction of the components in the panel
         */
        public static String getPosition(int position) {
            return switch (position) {
                case POSITION_TOP -> BorderLayout.NORTH;
                case POSITION_BOTTOM -> BorderLayout.SOUTH;
                case POSITION_LEFT -> BorderLayout.WEST;
                case POSITION_RIGHT -> BorderLayout.EAST;
                case POSITION_CENTER -> BorderLayout.CENTER;
                default -> throw new IllegalArgumentException("The position must be one of the following: POSITION_TOP, POSITION_BOTTOM, POSITION_LEFT, POSITION_RIGHT");
            };
        }

        private static GridLayout setDirectionLayout(int direction, int length) {
            return switch (direction) {
                case DirectionAndPosition.DIRECTION_ROW -> new GridLayout(1, length);
                case DirectionAndPosition.DIRECTION_COLUMN -> new GridLayout(length, 1);
                default -> throw new IllegalArgumentException("Invalid direction");
            };
        }
    }

    private class CustomComponent extends JPanel {
        
        public CustomComponent() {
            setLayout(new BorderLayout());
        }

        private void addButtons(JButton[] buttons, int direction) {
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(DirectionAndPosition.setDirectionLayout(direction, buttons.length));
            for (JButton jButton : buttons) {
                buttonPanel.add(jButton);
            }
            add(buttonPanel);
        }

        private void addLabels(JLabel[] labels, int direction) {
            JPanel labelPanel = new JPanel();
            labelPanel.setLayout(DirectionAndPosition.setDirectionLayout(direction, labels.length));
            for (JLabel jLabel : labels) {
                labelPanel.add(jLabel);
            }
            add(labelPanel);
        }

        private void addProgressBar(JProgressBar[] progressBars, int direction) {
            JPanel progressBarPanel = new JPanel();
            progressBarPanel.setLayout(DirectionAndPosition.setDirectionLayout(direction, progressBars.length));
            for (JProgressBar jProgressBar : progressBars) {
                progressBarPanel.add(jProgressBar);
            }
            add(progressBarPanel);
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
                Icon icon = new ColorIcon(bar.getColor(), barWidth, barHeight, 3);
                label.setIcon(icon);
                barPanel.add(label);

                JLabel barLabel = new JLabel(bar.getLabel());
                barLabel.setHorizontalAlignment(JLabel.CENTER);
                labelPanel.add(barLabel);
            }
        }
    }

    private class ColorIcon implements Icon {
    
        private Color color;
        private int width = 10;
        private int height = 10;
        private int shadow = 0;
    
        public ColorIcon(Color color) {
            this.color = color;
        }

        public ColorIcon(Color color, int width, int height) {
            this.color = color;
            this.width = width;
            this.height = height;
        }

        public ColorIcon(Color color, int width, int height, int shadow) {
            this.color = color;
            this.width = width;
            this.height = height;
            this.shadow = shadow;
        }
    
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            if (shadow > 0) {
                g.setColor(color);
                g.fillRect(x, y, width - shadow, height);
                g.setColor(Color.GRAY);
                g.fillRect(x + width - shadow, y + shadow, shadow, height - shadow);
            } else {
                g.setColor(color);
                g.fillRect(x, y, width, height); 
            }
        }
    
        @Override
        public int getIconWidth() {
            return width;
        }
 
        @Override
        public int getIconHeight() {
            return height;
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
