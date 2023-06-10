package View;

import Model.Solution;
import betterSwing.Section;
import betterSwing.Window;
import betterSwing.utils.DirectionAndPosition;
import javax.swing.JPanel;
import java.awt.GridLayout;
import java.awt.Color;
import utils.Config;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;

public class WindowStats {

	private Window window;
	private Solution[] solutions;

	public WindowStats(Solution[] solutions) {
		this.solutions = solutions;
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

	private DefaultPieDataset<String> createPieChartDataset(String categoryOne, String categoryTwo, boolean isPruned) {
		DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
		double averageOne = 0;
		double averageTwo = 0;
		for (Solution solution : this.solutions) {
			if (isPruned) {
				averageOne += solution.stats().getPruneCount();
				averageTwo += solution.stats().getStatesVisited() - solution.stats().getPruneCount();
			} else {
				averageOne += solution.stats().getMemoHit();
				averageTwo += solution.stats().getMemoRef() - solution.stats().getMemoHit();
			}
		}
		averageOne /= this.solutions.length;
		averageTwo /= this.solutions.length;
		dataset.setValue(categoryOne, averageOne);
		dataset.setValue(categoryTwo, averageTwo);
		return dataset;
	}

	private DefaultCategoryDataset createBarPlotDataset(boolean isTimes) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (int i = 0; i < this.solutions.length; i++) {
			Solution solution = this.solutions[i];
			if (isTimes) {
				dataset.addValue(solution.stats().getTimeSpent().toMillis(), solution.heuristic().name(), String.valueOf(i));
			} else {
				dataset.addValue(solution.movements().size(), String.valueOf(i), solution.heuristic().name());
			}
		}
		return dataset;
	}

	private ChartPanel createBarPlotChart(DefaultCategoryDataset dataset, String title) {
		// Create the chart
		JFreeChart chart = ChartFactory.createBarChart(
				title,
				"Ejecución",
				"Tiempo (ms)",
				dataset);

		// Display the chart
		return new ChartPanel(chart);
	}

	private ChartPanel createPieChart(DefaultPieDataset<String> dataset, String title, String categoryOne,
			String categoryTwo, Color colorOne, Color colorTwo) {
		// Create the chart
		JFreeChart chart = ChartFactory.createPieChart(
				title,
				dataset,
				true,
				true,
				false);

		// Customize the chart
		PiePlot plot = (PiePlot) chart.getPlot();
		plot.setSectionPaint(categoryOne, colorOne);
		plot.setSectionPaint(categoryTwo, colorTwo);

		// Display the chart
		return new ChartPanel(chart);
	}

	private ChartPanel createStackedBarPlotChart(DefaultCategoryDataset dataset, String title) {
		// Create the chart
		JFreeChart chart = ChartFactory.createStackedBarChart(
				title,
				"Distribución",
				"Número de movimientos",
				dataset);

		// Display the chart
		return new ChartPanel(chart);
	}

	private JPanel panelStats() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(2, 2));
		String categoryOne = "Podados";
		String categoryTwo = "Evaluados";
		DefaultPieDataset<String> pieCharDataset = this.createPieChartDataset(categoryOne, categoryTwo, true);
		panel.add(createPieChart(pieCharDataset, "Ratio de poda", categoryOne, categoryTwo,
				Color.MAGENTA, Color.CYAN));
		categoryOne = "Encontrados";
		categoryTwo = "No Encontrados";
		pieCharDataset = this.createPieChartDataset(categoryOne, categoryTwo, false);
		panel.add(createPieChart(pieCharDataset, "Ratio de \"Hit\" en la Memoización", categoryOne, categoryTwo,
				Color.PINK, Color.GREEN));
		DefaultCategoryDataset barPlotDataset = this.createBarPlotDataset(true);
		panel.add(createBarPlotChart(barPlotDataset, "Tiempos de ejecución"));
		barPlotDataset = this.createBarPlotDataset(false);
		panel.add(createStackedBarPlotChart(barPlotDataset, "Distribución de movimientos"));
		return panel;
	}

	public void show() {
		this.window.start();
	}
}
