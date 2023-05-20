package View;

import javax.swing.JPanel;
import java.awt.BorderLayout;

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
	private Long[] times;

	public WindowStats(Long[] times) {
		this.times = times;
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
		panel.setLayout(new BorderLayout());
		final String[] names = { "Tiempo de ejecución" };
		panel.add(this.createBarPlot(this.times, "Estadísticas tiempo ejecución", names, names), BorderLayout.CENTER);
		return panel;
	}

	private ChartPanel createBarPlot(Long[] values, String title, String[] columnNames, String[] rowNames) {
		// Create a dataset
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (int i = 0; i < values.length; i++) {
			String row = ("Row " + i);
			String column = ("Column " + i);
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
