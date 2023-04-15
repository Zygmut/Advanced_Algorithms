package View;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

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
		ChartPanel chartPanel = this.createPlot(values);
		ChartPanel chartPanel2 = this.createPlot(values);
		ChartPanel chartPanel3 = this.createPlot(values);
		ChartPanel chartPanel4 = this.createPlot(values);
		panel.add(chartPanel);
		panel.add(chartPanel2);
		panel.add(chartPanel3);
		panel.add(chartPanel4);
		return panel;
	}

	private ChartPanel createPlot(Double[] values) {
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

	public void show() {
		this.window.start();
	}

}
