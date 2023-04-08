package View;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import Master.MVC;
import Model.Point;
import Request.Notify;
import Request.Request;
import Request.RequestCode;
import betterSwing.Section;
import betterSwing.Window;
import betterSwing.utils.DirectionAndPosition;

public class View implements Notify {

	/**
	 * The MVC hub of the view.
	 */
	private MVC hub;
	/**
	 * The window of the view.
	 */
	private Window window;
	private int seed;
	private int pointAmount;

	/**
	 * This constructor creates a view with the MVC hub without any configuration
	 *
	 * @param mvc The MVC hub of the view.
	 * @see MVC
	 */
	public View(MVC mvc) {
		this.hub = mvc;
		this.window = new Window();
		this.window.initConfig();
		this.loadContent();
	}

	/**
	 * This constructor creates a view with the MVC hub and configures itself given
	 * a config path.
	 *
	 * @param mvc        The MVC hub of the view.
	 * @param configPath The path to its config.
	 * @see MVC
	 */
	public View(MVC mvc, String configPath) {
		this.hub = mvc;
		this.window = new Window(configPath);
		this.window.initConfig();
		this.loadContent();
	}

	@Override
	public void notifyRequest(Request request) {
		if (request.code != RequestCode.SHOW_DATA) {
			Logger.getLogger(this.getClass().getSimpleName())
					.log(Level.SEVERE, "{0} is not implemented.", request);
			return;
		}

		this.window.updateSection(body(), "Body", DirectionAndPosition.POSITION_CENTER);
	}

	/**
	 * Loads all the view content.
	 *
	 */
	private void loadContent() {
		window.addSection(header(), DirectionAndPosition.POSITION_TOP, "Header");
		window.addSection(body(), DirectionAndPosition.POSITION_CENTER, "Body");
	}

	private Section body() {
		Point[] data = this.hub.getModel().getData();
		XYSeries series = new XYSeries("Random Data");
		for (Point point : data) {
			series.add(point.x(), point.y());
		}

		XYDataset dataset = new XYSeriesCollection(series);

		// Create the chart
		JFreeChart chart = ChartFactory.createScatterPlot(
				"",
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

		plot.getDomainAxis().setFixedAutoRange(this.hub.getModel().getFrameDimension().width);
		plot.getRangeAxis().setFixedAutoRange(this.hub.getModel().getFrameDimension().height);

		// Create a frame to display the chart
		JPanel content = new JPanel();
		content.add(new ChartPanel(chart));
		Section body = new Section();
		body.createFreeSection(content);
		return body;
	}

	private Section header() {
		// Distribution dropdown
		JComboBox<String> distributionMenu = new JComboBox<>(new String[] { "Uniform", "Gaussian" });
		distributionMenu.setSelectedIndex(0);
		distributionMenu.addActionListener(e -> {
			String selectedValue = (String) distributionMenu.getSelectedItem();
			switch (selectedValue) {
				case "Uniform" -> {
					this.hub.getController().notifyRequest(new Request(RequestCode.GENERATE_UNIFORM_DATA, this));
				}
				case "Gaussian" -> {
					this.hub.getController().notifyRequest(new Request(RequestCode.GENERATE_GAUSSIAN_DATA, this));
				}
				default -> {
					Logger.getLogger(this.getClass().getSimpleName())
							.log(Level.SEVERE, "{0} is not implemented under the distribution menu.", selectedValue);
				}
			}
		});

		// Seed controller
		JLabel seedLabel = new JLabel("Seed: ");
		JSpinner seedSpinner = new JSpinner(
				new SpinnerNumberModel(this.hub.getModel().getSeed(), Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
		seedSpinner.addChangeListener(e -> {
			this.seed = (int) seedSpinner.getValue();
			this.hub.notifyRequest(new Request(RequestCode.UPDATE_SEED, this));
			String selectedValue = (String) distributionMenu.getSelectedItem();
			distributionMenu.getActionListeners()[0]
					.actionPerformed(new ActionEvent(seedSpinner, ActionEvent.ACTION_PERFORMED, selectedValue));
		});

		// Points controller
		JLabel pointLabel = new JLabel("Amount of points: ");
		JSpinner pointSpinner = new JSpinner(
				new SpinnerNumberModel(this.hub.getModel().getPointAmount(), Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
		pointSpinner.addChangeListener(e -> {
			this.pointAmount = (int) pointSpinner.getValue();
			this.hub.notifyRequest(new Request(RequestCode.UPDATE_AMOUNT, this));
			String selectedValue = (String) distributionMenu.getSelectedItem();
			distributionMenu.getActionListeners()[0]
					.actionPerformed(new ActionEvent(seedSpinner, ActionEvent.ACTION_PERFORMED, selectedValue));
		});

		// Start button
		JButton start = new JButton("Start");
		start.addActionListener(e -> {
			this.hub.notifyRequest(new Request(RequestCode.START, this));
		});

		JPanel content = new JPanel();
		content.add(distributionMenu);
		content.add(seedLabel);
		content.add(seedSpinner);
		content.add(pointLabel);
		content.add(pointSpinner);
		content.add(start);
		Section header = new Section();
		header.createFreeSection(content);
		return header;
	}

	/**
	 * Returns the window of the view.
	 *
	 * @return The window of the view.
	 */
	public Window getWindow() {
		return this.window;
	}

	/**
	 * Returns the current value of the seed in the UI.
	 *
	 * @return the current seed.
	 */
	public int getSeed() {
		return this.seed;
	}

	/**
	 * Returns the current amount of points in the Ui.
	 *
	 * @return the current amount of points.
	 */
	public int getPointAmount() {
		return this.pointAmount;
	}

}
