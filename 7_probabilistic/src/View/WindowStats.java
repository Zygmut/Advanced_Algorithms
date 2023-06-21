package View;

import betterSwing.Section;
import betterSwing.Window;
import betterSwing.utils.DirectionAndPosition;

import java.awt.GridLayout;
import java.time.Duration;
import java.util.logging.Logger;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import utils.Config;

public class WindowStats {
	private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

	private Window window;
	private Duration[][] stats;
	private long[] newtonFunction;

	public WindowStats(Duration[][] stats, long[] newtonFunction) {
		this.stats = stats;
		this.newtonFunction = newtonFunction;
		this.window = new Window(Config.VIEW_STATS_WIN_CONFIG_PATH);
		this.window.initConfig();
		this.loadContent();
	}

	private void loadContent() {
		this.window.addSection(
				this.sectionStats(),
				DirectionAndPosition.POSITION_CENTER,
				"Body");
	}

	private Section sectionStats() {
		Section section = new Section();
		section.createFreeSection(this.panelStats());
		return section;
	}

	private ChartPanel createBarPlot(Duration[] values, String title, String category, String value) {
		// Create a dataset
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (int i = 0; i < values.length; i++) {
			dataset.addValue(values[i].toMillis(), Long.toString(values[i].toMillis()), Integer.toString(i));
		}
		// Create a chart
		JFreeChart chart = ChartFactory.createBarChart(
				title, // chart title
				"Ejecución", // category axis label
				"Tiempo (ms)", // value axis label
				dataset // data
		);

		chart.removeLegend();
		// Create a chart panel
		return new ChartPanel(chart);
	}

	private JPanel panelStats() {
		final String[] lPanelNames = new String[] { "Encriptación", "Decriptación", "Primalidad", "Factorización",
				"Claves RSA" };
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(2, 3));

		for (int i = 0; i <= lPanelNames.length - 1; i++) {
			panel.add(this.createBarPlot(this.stats[i], lPanelNames[i], "Ejecución",
					"Tiempo (ms)"));
		}

		panel.add(this.createChartPanel(this.createDataset(this.newtonFunction),
				"Interpolación de Newton", "Número de dígitos", "Tiempo (horas)"));
		return panel;
	}

	private XYSeriesCollection createDataset(Duration[] set) {
		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries series = new XYSeries("Tiempo de ejecución");
		for (int i = 0; i < set.length; i++) {
			series.add(i, set[i].toMillis());
		}
		dataset.addSeries(series);
		return dataset;
	}

	private ChartPanel createChartPanel(XYSeriesCollection dataset, String title, String xAxisLabel,
			String yAxisLabel) {
		JFreeChart chart = ChartFactory.createXYLineChart(
				title,
				xAxisLabel,
				yAxisLabel,
				dataset,
				PlotOrientation.VERTICAL,
				true,
				true,
				false);
		return new ChartPanel(chart);
	}

	private XYSeriesCollection createDataset(long[] vals) {
		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries series = new XYSeries("Execution time");
		XYSeries linear = new XYSeries("Regression line");
		for (int i = 0; i < vals.length; i++) {
			series.add(i + 1, vals[i]);
		}

		// Calculate linear regression
		double n = vals.length;
		double sumX = n * (n + 1) / 2;
		double sumY = 0;
		double sumXY = 0;
		double sumXX = 0;
		for (int i = 0; i < vals.length; i++) {
			sumY += vals[i];
			sumXY += (i + 1) * vals[i];
			sumXX += (i + 1) * (i + 1);
		}
		double slope = (n * sumXY - sumX * sumY) / (n * sumXX - sumX * sumX);
		double intercept = (sumY - slope * sumX) / n;
		for (int i = 0; i < vals.length; i++) {
			linear.add(i + 1, slope * (i + 1) + intercept);
		}

		// Calculate R^2
		double mean = sumY / n;
		double ssTot = 0;
		double ssRes = 0;
		for (int i = 0; i < vals.length; i++) {
			ssTot += Math.pow(vals[i] - mean, 2);
			ssRes += Math.pow(vals[i] - (slope * (i + 1) + intercept), 2);
		}
		assert ssTot > 0;

		logger.info("R^2: " + Double.toString(1 - ssRes / ssTot));

		dataset.addSeries(series);
		dataset.addSeries(linear);
		return dataset;
	}

	public void show() {
		this.window.start();
	}
}
