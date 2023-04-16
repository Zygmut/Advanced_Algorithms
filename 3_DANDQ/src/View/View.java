package View;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerNumberModel;

import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.LegendItemSource;
import org.jfree.chart.annotations.XYLineAnnotation;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import Master.MVC;
import Model.Distribution;
import Model.PairPoint;
import Model.Point;
import Request.Body;
import Request.BodyCode;
import Request.Notify;
import Request.Request;
import Request.RequestCode;
import Request.RequestType;
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
	public void notifyRequest(Request<?> request) {
		switch (request.code) {
			case SHOW_DATA -> {
				this.window.updateSection(
						body((Point[]) request.body.get(BodyCode.DATA),
								new PairPoint[] {},
								new PairPoint[] {}),
						"Body", DirectionAndPosition.POSITION_CENTER);
			}
			case RESULT_MIN_DIS, RESULT_MAX_DIS -> {
				Object[] data = (Object[]) request.body.get(BodyCode.PAIR_POINTS);
				this.window.updateSection(body((Point[]) data[0], (PairPoint[]) data[1], (PairPoint[]) data[2]), "Body",
						DirectionAndPosition.POSITION_CENTER);
			}
			case STATS_DATA -> {
				Object[] data = (Object[]) request.body.get(BodyCode.DATA);
				WindowStats windowStats = new WindowStats(data);
				windowStats.show();
			}
			default -> {
				Logger.getLogger(this.getClass().getSimpleName())
						.log(Level.SEVERE, "{0} is not implemented.", request);
			}
		}
	}

	/**
	 * Loads all the view content.
	 *
	 */
	private void loadContent() {
		this.window.addSection(this.header(), DirectionAndPosition.POSITION_TOP, "Header");
		this.window.addSection(this.body(new Point[] {}, new PairPoint[] {}, new PairPoint[] {}),
				DirectionAndPosition.POSITION_CENTER, "Body");
		this.window.addSection(this.footer(), DirectionAndPosition.POSITION_BOTTOM, "Footer");
	}

	private JMenuBar menu() {
		JMenuBar menuBar = new JMenuBar();
		JMenu options = new JMenu("Opciones");
		JMenuItem stats = new JMenuItem("Estadísticas");
		stats.addActionListener(e -> this.hub.notifyRequest(new Request<>(RequestCode.STATS_DATA, this)));
		options.add(stats);
		menuBar.add(options);
		return menuBar;
	}

	private Section footer() {
		Section buttonSection = new Section();
		JButton[] buttons = new JButton[7];
		buttons[0] = new JButton("Dist Min N^2");
		buttons[0].addActionListener(e -> this.hub.notifyRequest(new Request<>(RequestCode.CALC_MIN_DIS, this)));
		buttons[1] = new JButton("Dist Máx N^2");
		buttons[1].addActionListener(e -> this.hub.notifyRequest(new Request<>(RequestCode.CALC_MAX_DIS, this)));
		buttons[2] = new JButton("Dist Min NlogN");
		buttons[2].addActionListener(e -> this.hub.notifyRequest(new Request<>(RequestCode.CALC_MIN_DIS_NLOGN, this)));
		buttons[3] = new JButton("Dist Máx NlogN");
		buttons[3].addActionListener(e -> this.hub.notifyRequest(new Request<>(RequestCode.CALC_MAX_DIS_NLOGN, this)));
		buttons[4] = new JButton("Borrar datos");
		buttons[4].addActionListener(e -> this.hub.notifyRequest(new Request<>(RequestCode.CLEAR_DATA, this)));
		buttons[5] = new JButton("Ver estadísticas");
		buttons[5].addActionListener(e -> {
			this.hub.notifyRequest(new Request<>(RequestCode.CALC_STATS, this));
		});
		buttons[6] = new JButton("Auto");
		buttons[6].addActionListener(e -> {
			// TODO: Adapt to all the algorithms
			String txt = buttons[6].getText();
			System.out.println(txt);
			if (txt.equals("Auto")) {
				buttons[6].setText("Stop");
				this.hub.notifyRequest(new Request<>(RequestCode.CALC_AUTO, this));
			} else {
				buttons[6].setText("Auto");
				this.hub.notifyRequest(new Request<>(RequestCode.STOP_AUTO, this));
			}
		});
		buttonSection.createButtons(buttons, DirectionAndPosition.DIRECTION_ROW);
		return buttonSection;
	}

	private Section body(Point[] data, PairPoint[] minPairPoints, PairPoint[] maxPairPoints) {
		ScatterPlot scatterPlot = new ScatterPlot(Color.MAGENTA);
		JPanel content = new JPanel();
		content.setLayout(new BorderLayout());
		int width = this.hub.getModel().getFrameDimension().width;
		int height = this.hub.getModel().getFrameDimension().height;
		content.add(new ChartPanel(scatterPlot.createPlot(data, width, height, minPairPoints, maxPairPoints)),
				BorderLayout.CENTER);
		Section body = new Section();
		body.createFreeSection(content);
		return body;
	}

	private Section header() {
		// Distribution dropdown
		JLabel distLabel = new JLabel("Distribución: ");
		String[] distributions = Arrays.stream(Distribution.values()).map(Enum::name).toArray(String[]::new);
		JComboBox<String> distributionMenu = new JComboBox<>(distributions);
		distributionMenu.setSelectedIndex(0);
		distributionMenu.addActionListener(e -> {
			Distribution selectedValue = Distribution.valueOf((String) distributionMenu.getSelectedItem());
			switch (selectedValue) {
				case UNIFORM -> {
					this.hub.notifyRequest(new Request<>(RequestCode.GENERATE_UNIFORM_DATA, this));
				}
				case GUASSIAN -> {
					this.hub.notifyRequest(new Request<>(RequestCode.GENERATE_GAUSSIAN_DATA, this));
				}
				case POISSON -> {
					this.hub.notifyRequest(new Request<>(RequestCode.GENERATE_POISSON_DATA, this));
				}
				case EXPONENTIAL -> {
					this.hub.notifyRequest(new Request<>(RequestCode.GENERATE_EXPONENTIAL_DATA, this));
				}
				default -> {
					Logger.getLogger(this.getClass().getSimpleName())
							.log(Level.SEVERE, "{0} is not implemented under the distribution menu.", selectedValue);
				}
			}
			this.hub.notifyRequest(new Request<>(RequestCode.CLEAR_SOLUTIONS, this));
		});

		// Seed controller
		JLabel seedLabel = new JLabel("Semilla: ");
		JSpinner seedSpinner = new JSpinner(
				new SpinnerNumberModel(this.hub.getModel().getSeed(), Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
		seedSpinner.addChangeListener(e -> {

			Body<Integer> body = new Body<>(RequestType.POST, BodyCode.SEED, (int) seedSpinner.getValue());
			this.hub.notifyRequest(new Request<>(RequestCode.UPDATE_SEED, this, body));
			String selectedValue = (String) distributionMenu.getSelectedItem();
			distributionMenu.getActionListeners()[0]
					.actionPerformed(new ActionEvent(seedSpinner, ActionEvent.ACTION_PERFORMED, selectedValue));
		});
		JComponent editor = seedSpinner.getEditor();
		Dimension ps = editor.getPreferredSize();
		ps.width = 100;
		editor.setPreferredSize(ps);

		// Points controller
		JLabel pointLabel = new JLabel("Número de puntos: ");
		JSpinner pointSpinner = new JSpinner(
				new SpinnerNumberModel(this.hub.getModel().getPointAmount(), Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
		pointSpinner.addChangeListener(e -> {
			Body<Integer> body = new Body<>(RequestType.POST, BodyCode.POINT_AMOUNT, (int) pointSpinner.getValue());
			this.hub.notifyRequest(new Request<>(RequestCode.UPDATE_AMOUNT, this, body));
			String selectedValue = (String) distributionMenu.getSelectedItem();
			distributionMenu.getActionListeners()[0]
					.actionPerformed(new ActionEvent(seedSpinner, ActionEvent.ACTION_PERFORMED, selectedValue));
		});

		editor = pointSpinner.getEditor();
		ps = editor.getPreferredSize();
		ps.width = 100;
		editor.setPreferredSize(ps);

		// N solutions
		JLabel solutionLabel = new JLabel("Número de Soluciones: ");
		JSpinner solutionSpinner = new JSpinner(
				new SpinnerNumberModel(this.hub.getModel().getNSolutions(), 0, Integer.MAX_VALUE, 1));
		solutionSpinner.addChangeListener(e -> {
			Body<Integer> body = new Body<>(RequestType.POST, BodyCode.SOLUTION_AMOUNT,
					(int) solutionSpinner.getValue());
			this.hub.notifyRequest(new Request<>(RequestCode.UPDATE_SOLUTIONS, this, body));
		});
		editor = solutionSpinner.getEditor();
		ps = editor.getPreferredSize();
		ps.width = 100;
		editor.setPreferredSize(ps);

		JPanel content = new JPanel();
		content.add(distLabel);
		content.add(distributionMenu);
		content.add(seedLabel);
		content.add(seedSpinner);
		content.add(pointLabel);
		content.add(pointSpinner);
		content.add(solutionLabel);
		content.add(solutionSpinner);
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

	private class ScatterPlot {

		private Color seriesColor;

		public ScatterPlot(Color seriesColor) {
			this.seriesColor = seriesColor;
		}

		private JFreeChart createPlot(Point[] data, int width, int height, PairPoint[] minPairPoints,
				PairPoint[] maxPairPoints) {
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
			renderer.setSeriesPaint(0, this.seriesColor);
			plot.setRenderer(renderer);
			for (PairPoint pairPoint : minPairPoints) {
				XYLineAnnotation line = new XYLineAnnotation(
						pairPoint.p1().x(), pairPoint.p1().y(), // x and y coordinates of point 1
						pairPoint.p2().x(), pairPoint.p2().y(), // x and y coordinates of point 2
						new BasicStroke(1.0f),
						Color.BLUE);
				plot.addAnnotation(line);
			}
			for (PairPoint pairPoint : maxPairPoints) {
				XYLineAnnotation line = new XYLineAnnotation(
						pairPoint.p1().x(), pairPoint.p1().y(), // x and y coordinates of point 1
						pairPoint.p2().x(), pairPoint.p2().y(), // x and y coordinates of point 2
						new BasicStroke(1.0f),
						Color.RED);
				plot.addAnnotation(line);
			}
			plot.getDomainAxis().setRange(0, width);
			plot.getRangeAxis().setRange(0, height);
			chart.removeLegend();
			return chart;
		}
	}

}
