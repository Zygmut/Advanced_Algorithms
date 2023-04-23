package View;

import java.awt.GridLayout;
import java.util.Arrays;

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
		Section section = new Section();
		section.createFreeSection(this.panelStats());
		return section;
	}

	private JPanel panelStats() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(3, 3));

		String[] names = { "Media dist.", "Máx. dist.", "Min. dist." };
		String[] names2 = { "Media máx.", "Media min." };

		// N^2
		// -> Max
		Double[] data = new Double[] {
				this.values[0],
				this.values[1],
				this.values[2]
		};
		ChartPanel nnMax = this.createBarPlot(data, "N^2 Máx.", names, names);
		// -> Min
		data = new Double[] {
				this.values[3],
				this.values[4],
				this.values[5]
		};
		System.out.println(Arrays.toString(data));
		ChartPanel nnMin = this.createBarPlot(data, "N^2 Min.", names, names);
		// NlogN
		// -> Max
		data = new Double[] {
				this.values[6],
				this.values[7],
				this.values[8]
		};
		ChartPanel nlognMax = this.createBarPlot(data, "NlogN Máx.", names, names);
		// -> Min
		data = new Double[] {
				this.values[9],
				this.values[10],
				this.values[11]
		};
		ChartPanel nlognMin = this.createBarPlot(data, "NlogN Min.", names, names);
		data = new Double[] {
				this.values[12],
				this.values[13],
		};
		String[] names3 = { "Tiempo medio máx.", "Tiempo medio min." };
		ChartPanel nnTime = this.createBarPlot(data, "N^2 Tiempos", names2, names3);
		data = new Double[] {
				this.values[14],
				this.values[15],
		};
		ChartPanel nlognTime = this.createBarPlot(data, "NlogN Tiempos", names2, names3);
		data = new Double[] {
				this.values[0],
				this.values[1],
				this.values[2],
				this.values[6],
				this.values[7],
				this.values[8]
		};
		String[] names4 = {
				"Media dist. máx. N^2", "N^2 Máx.", "N^2 Min.",
				"Media dist. máx. NlogN", "NlogN Máx.", "NlogN Min." };
		String[] names5 = {
				"Media dist.", "Máx. dist.", "Min. dist.",
				"Media dist.", "Máx. dist.", "Min. dist.",
		};
		ChartPanel nnVsnlognMax = this.createStackedBarPlot(data, "N^2 vs NlogN Máx.", names4, names5);
		data = new Double[] {
				this.values[3],
				this.values[4],
				this.values[5],
				this.values[9],
				this.values[10],
				this.values[11]
		};
		names4 = new String[] {
				"Media dist. min. N^2", "N^2 Máx.", "N^2 Min.",
				"Media dist. min. NlogN", "NlogN Máx.", "NlogN Min."
		};
		ChartPanel nnVsnlognMin = this.createStackedBarPlot(data, "N^2 vs NlogN Min.", names4, names5);
		data = new Double[] {
				this.values[12],
				this.values[13],
				this.values[14],
				this.values[15]
		};
		String[] names6 = {
				"Tiempo medio máx. N^2 ", "Tiempo medio min. N^2",
				"Tiempo medio máx. NLogN", "Tiempo medio min. NlogN" };
		String[] names7 = {
				"Media tiempos máx", "Media tiempos min",
				"Media tiempos máx", "Media tiempos min"
		};
		ChartPanel nnVsnlognTime = this.createStackedBarPlot(data, "N^2 vs NlogN Tiempos", names6, names7);

		panel.add(nnMax);
		panel.add(nnMin);
		panel.add(nnTime);
		panel.add(nlognMax);
		panel.add(nlognMin);
		panel.add(nlognTime);
		panel.add(nnVsnlognMax);
		panel.add(nnVsnlognMin);
		panel.add(nnVsnlognTime);

		return panel;
	}

	private ChartPanel createStackedBarPlot(Double[] values, String title, String[] seriesName,
			String[] categoriesNames) {
		// Create a dataset
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (int i = 0; i < values.length; i++) {
			String series = seriesName == null ? ("Series " + i) : seriesName[i];
			String category = categoriesNames == null ? ("Category " + i) : categoriesNames[i];
			dataset.addValue(values[i], series, category);
		}
		// Create a chart
		JFreeChart chart = ChartFactory.createStackedBarChart(
				title, // chart title
				"Category", // category axis label
				"Value", // value axis label
				dataset // data
		);
		// Create a chart panel
		ChartPanel chartPanel = new ChartPanel(chart);
		return chartPanel;
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
