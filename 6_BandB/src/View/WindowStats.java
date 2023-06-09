package View;

import java.util.Arrays;
import java.util.function.LongUnaryOperator;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeriesCollection;

import Model.Solution;
import betterSwing.Section;
import betterSwing.Window;
import betterSwing.utils.DirectionAndPosition;
import utils.Config;

public class WindowStats {

	private Window window;

	public WindowStats(Solution[] solutions) {
		System.out.println(Arrays.deepToString(solutions));
		this.window = new Window(Config.VIEW_STATS_WIN_CONFIG_PATH);
		this.window.initConfig();
		this.loadContent();
	}

	private void loadContent() {
		this.window.addSection(this.sectionStats(), DirectionAndPosition.POSITION_CENTER, "Body");
	}

	private Section sectionStats() {
		Section section = new Section();
		section.createFreeSection(this.panelStats());
		return section;
	}

	private JPanel panelStats() {
		JPanel panel = new JPanel();

		return panel;
	}

	private XYSeriesCollection createDataSet() {
		LongUnaryOperator toMB = n -> n / 1024 / 1024;
		XYSeriesCollection dataset = new XYSeriesCollection();

		return dataset;
	}

	private ChartPanel createLineChartPanel(XYSeriesCollection dataset, String title, String xLabel, String yLabel) {
		// Create chart
		JFreeChart chart = ChartFactory.createXYLineChart(title, xLabel, yLabel, dataset);
		// Create Panel
		return new ChartPanel(chart);
	}

	private ChartPanel createBarPlot(Double[] values, String title, String[] columnNames, String[] rowNames) {
		// Create a dataset
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (int i = 0; i < values.length; i++) {
			String row = rowNames == null ? ("Row " + i) : rowNames[i];
			String column = columnNames == null ? ("Column " + i) : columnNames[i];
			dataset.addValue(values[i], row, column);
		}
		// Create a chart
		JFreeChart chart = ChartFactory.createBarChart(
				title, // chart title
				"Categoría", // category axis label
				"Valor", // value axis label
				dataset // data
		);
		// Create a chart panel
		return new ChartPanel(chart);
	}

	public void show() {
		this.window.start();
	}

}
