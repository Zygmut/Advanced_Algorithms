package Master;

import mesurament.Mesurament;
import Request.Request;
import Request.RequestCode;
import java.awt.Color;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import Model.Point;

public class Main {
	private static final boolean DEBUG = true;

	public static void main(String[] args) {
		if (!DEBUG)
			Mesurament.mesura();
		MVC mvc = new MVC("./config.json");
		mvc.getController().setSeed(27);
		mvc.show();
	}
}
