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

	public WindowStats(Solution[] solutions) {
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

	private DefaultPieDataset createPieChartDataset() {
		DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Category 1", 30.0);
        dataset.setValue("Category 2", 20.0);
        dataset.setValue("Category 3", 50.0);
		return dataset;
	}

	private DefaultCategoryDataset createBarPlotDataset() {
		return null;
	}

	private ChartPanel createBarPlotChart(DefaultCategoryDataset dataset, String title) {
		return null;
	}

	private ChartPanel createPieChart(DefaultPieDataset dataset, String title) {
		 // Create the chart
        JFreeChart chart = ChartFactory.createPieChart(
                "Pie Chart",
                dataset,
                true,
                true,
                false
        );

        // Customize the chart
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionPaint("Category 1", Color.RED);
        plot.setSectionPaint("Category 2", Color.GREEN);
        plot.setSectionPaint("Category 3", Color.BLUE);

        // Display the chart
        return new ChartPanel(chart);
	}

	private ChartPanel createStackedBarPlotChart(DefaultCategoryDataset dataset, String title) {
		return null;
	}

	private JPanel panelStats() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(2, 2));
		DefaultPieDataset pieCharDataset = this.createPieChartDataset();
		panel.add(createPieChart(pieCharDataset, "Ratio (Visitados/Podados)"));
		pieCharDataset = this.createPieChartDataset();
		panel.add(createPieChart(pieCharDataset, "Ratio (Referenciados/Encontrados)"));
		DefaultCategoryDataset barPlotDataset = this.createBarPlotDataset();
		panel.add(createBarPlotChart(barPlotDataset, "Tiempo de ejecución"));
		barPlotDataset = this.createBarPlotDataset();
		panel.add(createStackedBarPlotChart(barPlotDataset, "Distribución de movimientos"));
		return panel;
	}

	public void show() {
		this.window.start();
	}
}
