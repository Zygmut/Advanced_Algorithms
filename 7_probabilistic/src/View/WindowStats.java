package View;

import betterSwing.Section;
import betterSwing.Window;
import betterSwing.utils.DirectionAndPosition;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import Model.Result;
import utils.Config;

public class WindowStats {

	private Window window;
	private Result[] result;

	public WindowStats(Result[] result) {
		this.result = result;
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

	private JPanel panelStats() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		ChartPanel chartPanel = this.createChartPanel(this.createDataset(),
				"Estadísticas", "Ejecución", "Tiempo (ms)");
		panel.add(chartPanel, BorderLayout.CENTER);
		return panel;
	}

	private XYSeriesCollection createDataset() {
		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries series = new XYSeries("Tiempo de ejecución");
		for (int i = 0; i < this.result.length; i++) {
			series.add(i, this.result[i].time().toMillis());
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

	public void show() {
		this.window.start();
	}
}
