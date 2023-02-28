package View;

import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.net.URL;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.MatteBorder;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

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

    /**
     * The panel that will be returned by the getPanel() method if the component 
     * does not render HTML.
     */
    private JPanel panel;
    /**
     * The component that will be returned by the getComponent() method if the 
     * component renders HTML.
     */
    private Component component;
    /**
     * Tells if the component renders HTML or not.
     */
    private boolean isHTML = false;


    /**
     * Creates a new Section object.
     */
    public Section() {
        this.panel = new JPanel();
        this.component = null; 
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
     * @param labels    The labels of the columns
     * @param values    The values of the columns
     * @param colors    The colors of the columns
     * @param lineNames The names of the lines
     * @param title     The title of the chart
     */
    public void createLineChart(String[] labels, long[][] values, Color[] colors, String[] lineNames, String title) {
        assert labels.length == 2; // X e Y
        MultiLineChart chart;
        if (colors == null) {
            checkIfArraysAreValid(lineNames.length, values.length);
            chart = new MultiLineChart(lineNames, labels[0], labels[1], title);
        } else {
            checkIfArraysAreValid(lineNames.length, values.length, colors.length);
            chart = new MultiLineChart(lineNames, labels[0], labels[1], title);
            // TODO: Allow to set colors
            //chart = new MultiLineChart(lineNames, labels[0], labels[1], title, colors);
        }
        chart.addLineChart(values);
        this.panel = chart.createChartPanel();
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

    /**
     * This method allows to add a custom component to the section. The Object will 
     * be converted into a JPanel. Basically, this method can convert a JPanel or 
     * similars to a Section.
     * 
     * @param panel The custom component to add
     */
    public void createSectionOnSection(JPanel panel) {
        this.panel = panel;
    }

    /**
     * Creates a Section from a HTML String. The Object Section will convert into 
     * a Component. Some CSS inlines properties are supported.
     * 
     * @param html The HTML String
     */
    public void createSectionFromHTML(String html) {
        JEditorPane editorPane = new JEditorPane();
        editorPane.setEditable(false);
        editorPane.setContentType("text/html");
        editorPane.setText(html);
        this.component = editorPane;
        this.isHTML = true;
    }

    /**
     * Creates a Section from a HTML File. The Object Section will convert into a
     * Component. For loading the HTML file, the URL must be valid. If the URL is
     * local, the URL must be like this: "file://path/to/file.html". The content of
     * the html file can support som CSS properties.
     * 
     * @param url The URL of the HTML file
     */
    public void createSectionFromHTMLFile(URL url) {
        JEditorPane editorPane = new JEditorPane();
        editorPane.setEditable(false);
        editorPane.setContentType("text/html");
        try {
            editorPane.setPage(url);
        } catch (IOException ex) {
            throw new IllegalArgumentException("The URL is not valid");
        }
        this.component = editorPane;
        this.isHTML = true;
    }

    /**
     * Creates a free section. The Object Section will convert into a JPanel with 
     * the free section. Basically, allows the user to transform a JPanel into a
     * Section.
     *
     * @param panel The panel to add
     */
    public void createFreeSection(JPanel panel) {
        this.panel = panel;
    }

    /**
     * This method checks if the arrays have the same length. If not, it throws an
     * IllegalArgumentException.
     * 
     * @param lengths The lengths of the arrays
     */
    private void checkIfArraysAreValid(int... lengths) {
        for (int i = 0; i < lengths.length - 1; i++) {
            if (lengths[i] != lengths[i + 1]) {
                throw new IllegalArgumentException("The arrays must have the same length, one or more parameters are not the same length");
            }
        }
    }

    /**
     * Returns the panel of the section.
     * 
     * @return The panel of the section
     */
    public JPanel getPanel() {
        return this.panel;
    }

    /**
     * Returns true if the section is a HTML section.
     * 
     * @return True if the section is a HTML section
     */
    public boolean isHTML() {
        return this.isHTML;
    }

    /**
     * Returns the component of the section.
     * 
     * @return The component of the section
     */
    public Component getComponent() {
        return this.component;
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

    public class MultiLineChart {
        private String[] labels;
        private String xLabels;
        private String yLabels;
        private String title;
        private JFreeChart chart;

        public MultiLineChart(String[] labels, String xLabels, String yLabels, String title) {
            super();
            this.labels = labels;
            this.xLabels = xLabels;
            this.yLabels = yLabels;
            this.title = title;
        }

        private JPanel createChartPanel() {
            return new ChartPanel(this.chart);
        }

        private void addLineChart(long[][] data) {
            XYDataset dataset = createDataSets(data);
            this.chart = ChartFactory.createXYLineChart(this.title, 
                this.xLabels, this.yLabels, dataset);
        }

        private XYDataset createDataSets(long[][] data) {
            XYSeriesCollection dataset = new XYSeriesCollection();
            for (int i = 0; i < data.length; i++) {
                XYSeries series = new XYSeries(labels[i]);
                for (int j = 0; j < data[i].length; j++) {
                    series.add(j, data[i][j]);
                }
                dataset.addSeries(series);
            }
            return dataset;
        }

        private void addLineChart(double[][] data) {
            XYDataset dataset = createDataSets(data);
            chart = ChartFactory.createXYLineChart(this.title, 
                this.xLabels, this.yLabels, dataset);
        }

        private XYDataset createDataSets(double[][] data) {
            XYSeriesCollection dataset = new XYSeriesCollection();
            for (int i = 0; i < data.length; i++) {
                XYSeries series = new XYSeries(labels[i]);
                for (int j = 0; j < data[i].length; j++) {
                    series.add(j, data[i][j]);
                }
                dataset.addSeries(series);
            }
            return dataset;
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
            super();
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
            super();
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
