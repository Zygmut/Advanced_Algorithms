package View;

import java.awt.GridLayout;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import betterSwing.Section;
import betterSwing.Window;
import betterSwing.utils.DirectionAndPosition;
import utils.Config;

public class WindowStats {

	private Window window;
	private Double[] values;

	public WindowStats(Object[] data) {
		this.window = new Window(Config.CONFIG_PATH_TO_STATS_WINDOW);
		this.window.initConfig();
		this.values = new Double[data.length];
		for (int i = 0; i < data.length; i++) {
			this.values[i] = (Double) data[i];
		}
		this.loadContent();
	}

	private void loadContent() {
		this.window.addSection(this.sectionStats(), DirectionAndPosition.POSITION_CENTER, "Body");
	}

	private Section sectionStats() {
		// TODO: #37 Create a section with the stats.
		Section section = new Section();
		section.createFreeSection(this.panelStats());
		return section;
	}

	private JPanel panelStats() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(2, 2));
		ChartPanel chartPanel = this.createBarPlot(values);
		ChartPanel chartPanel2 = this.createLinePlot(values);
		ChartPanel chartPanel3 = this.createPiePlot(values);
		ChartPanel chartPanel4 = this.createScatterPlot(values);
		panel.add(chartPanel);
		panel.add(chartPanel2);
		panel.add(chartPanel3);
		panel.add(chartPanel4);
		return panel;
	}

	private ChartPanel createBarPlot(Double[] values) {
		// Create a dataset
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (int i = 0; i < values.length; i++) {
			dataset.addValue(values[i], "Media " + i, "Media " + i);
		}
		// Create a chart
		JFreeChart chart = ChartFactory.createBarChart(
				"Estadísticas de ...", // chart title
				"Category", // category axis label
				"Value", // value axis label
				dataset // data
		);
		// Create a chart panel
		ChartPanel chartPanel = new ChartPanel(chart);
		return chartPanel;
	}

	private ChartPanel createLinePlot(Double[] values) {
		// Create a dataset
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (int i = 0; i < values.length; i++) {
			dataset.addValue(values[i], "Media " + i, "Media " + i);
		}
		// Create a chart
		JFreeChart chart = ChartFactory.createLineChart(
				"Estadísticas de ...", // chart title
				"Category", // category axis label
				"Value", // value axis label
				dataset // data
		);
		// Create a chart panel
		ChartPanel chartPanel = new ChartPanel(chart);
		return chartPanel;
	}

	private ChartPanel createPiePlot(Double[] values) {
		// Create a dataset
		DefaultPieDataset dataset = new DefaultPieDataset();
		dataset.setValue("Category 1", 30);
		dataset.setValue("Category 2", 20);
		dataset.setValue("Category 3", 50);

		// Create a chart
		JFreeChart chart = ChartFactory.createPieChart(
				"Pie Chart Demo",
				dataset,
				true,
				true,
				false);
		// Create a chart panel
		ChartPanel chartPanel = new ChartPanel(chart);
		return chartPanel;
	}

	private ChartPanel createScatterPlot(Double[] values) {
		XYSeries series = new XYSeries("Data");
		series.add(1.0, 1.0);
		series.add(2.0, 4.0);
		series.add(3.0, 3.0);
		series.add(4.0, 5.0);
		series.add(5.0, 7.0);
		XYDataset dataset = new XYSeriesCollection(series);

		// Create a chart
		JFreeChart chart = ChartFactory.createScatterPlot(
				"Scatter Plot Demo",
				"X",
				"Y",
				dataset,
				PlotOrientation.VERTICAL,
				true,
				true,
				false);
		// Create a chart panel
		ChartPanel chartPanel = new ChartPanel(chart);
		return chartPanel;
	}

	public void show() {
		this.window.start();
	}

}
