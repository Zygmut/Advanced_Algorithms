package View;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Label;
import java.util.List;
import java.util.function.LongUnaryOperator;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import Model.Statistics.AtomIteration;
import Model.Statistics.Statistics;

import betterSwing.Section;
import betterSwing.Window;
import betterSwing.utils.DirectionAndPosition;
import utils.Config;

public class WindowStats {

	private Window window;
	private Statistics statistics;

	public WindowStats(Statistics statistics) {
		this.statistics = statistics;
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
		LongUnaryOperator toMB = n -> n / 1024 / 1024;

		String title = String.format("Estadísticas %s con calculo distancia %s", this.statistics.getAlgorithm(), this.statistics.getDistanceType());

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setBackground(Color.BLACK);

		JPanel panelTitle = new JPanel();
		panelTitle.setBackground(Color.WHITE);
		Label label = new Label(title);
		label.setAlignment(Label.CENTER);
		label.setFont(new Font("Arial", Font.BOLD, 20));
		panelTitle.add(label);
		panel.add(panelTitle, BorderLayout.NORTH);

		JPanel panelData = new JPanel();
		panelData.setLayout(new GridLayout(1, 2, 1, 0));
		panelData.setBackground(Color.BLACK);
		XYSeriesCollection dataset = createDataSet();
		ChartPanel chartPanel = createLineChartPanel(dataset, "Por iteración", "Iteración", "Valor");
		panelData.add(chartPanel);

		double time = this.statistics.getTime() / 1000;
		Double[] values = {
				time,
				this.statistics.getNodes() * 1.0,
				toMB.applyAsLong(this.statistics.getMemoryUsed()) * 1.0,
				this.statistics.getIterations() * 1.0,
				this.statistics.getNumberOfVisitedNodes() * 1.0,
		};

		String[] columnNames = {
				"Tiempo",
				"Nodos",
				"Memoria",
				"Iteraciones",
				"Nodos visitados",
		};
		String[] rowNames = {
				"Tiempo",
				"Nodos",
				"Memoria",
				"Iteraciones",
				"Nodos visitados",
		};
		chartPanel = createBarPlot(values, "Globales", columnNames, rowNames);
		panelData.add(chartPanel);

		panel.add(panelData, BorderLayout.CENTER);

		return panel;
	}

	private XYSeriesCollection createDataSet() {
		LongUnaryOperator toMB = n -> n / 1024 / 1024;
		XYSeriesCollection dataset = new XYSeriesCollection();
		List<List<AtomIteration>> dataPerIteration = this.statistics.getDataPerIteration();
		for (int i = 0; i < dataPerIteration.size(); i++) {
			List<AtomIteration> data = dataPerIteration.get(i);
			XYSeries seriesIt = new XYSeries("Iteración (" + i + ")");
			XYSeries seriesNodes = new XYSeries("Nodos visitados (" + i + ")");
			XYSeries seriesMemory = new XYSeries("Memoria usada (" + i + ")");
			for (int j = 0; j < data.size(); j++) {
				AtomIteration atom = data.get(j);
				seriesIt.add(j, atom.interation());
				seriesNodes.add(j, atom.numberOfVisitedNodes());
				seriesMemory.add(j, toMB.applyAsLong(atom.memoryUsed()));
			}
			dataset.addSeries(seriesIt);
			dataset.addSeries(seriesNodes);
			dataset.addSeries(seriesMemory);
		}
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
		ChartPanel chartPanel = new ChartPanel(chart);
		return chartPanel;
	}

	public void show() {
		this.window.start();
	}

}
