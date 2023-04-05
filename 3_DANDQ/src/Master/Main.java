package Master;

import Request.Request;
import Request.RequestCode;
import mesurament.Mesurament;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Random;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.Range;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import Model.Point;

public class Main {

	public static void main(String[] args) throws Exception {
		// Mesurament.mesura();
		MVC mvc = new MVC();
		mvc.getController().setSeed(27);

		mvc.getView().initConfig("config.txt");
		mvc.notifyRequest(new Request(RequestCode.GENERATE_GAUSSIAN_DATA, "Main"));
		// Generate some random data

		// Create an XYDataset from the data
		Point[] data = mvc.getModel().getData();
		XYSeries series = new XYSeries("Random Data");
		for (Point point : data) {
			series.add(point.x(), point.y());
		}

		XYDataset dataset = new XYSeriesCollection(series);

		// Create the chart
		JFreeChart chart = ChartFactory.createScatterPlot(
				"Random Data Plot",
				"X",
				"Y",
				dataset);

		// Customize the plot and renderer
		XYPlot plot = chart.getXYPlot();
		plot.setInsets(new RectangleInsets(0, 0, 0, 0));
		plot.setBackgroundPaint(Color.WHITE);
		plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
		plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
		plot.setDomainZeroBaselineVisible(true);
		plot.setRangeZeroBaselineVisible(true);
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(false, true);
		renderer.setSeriesPaint(0, Color.BLUE);
		plot.setRenderer(renderer);

		plot.getDomainAxis().setFixedAutoRange(mvc.getModel().getFrameDimension().width);
		plot.getRangeAxis().setFixedAutoRange(mvc.getModel().getFrameDimension().height);

		// Create a frame to display the chart
		ChartFrame frame = new ChartFrame("Random Data Plot", chart);
		frame.pack();
		frame.setVisible(true);

	}
}
