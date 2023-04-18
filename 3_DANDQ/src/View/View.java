package View;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.geom.Ellipse2D;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYLineAnnotation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
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
				PairPoint[] data = {};
				this.window.updateSection(
						body((Point[]) request.body.get(BodyCode.DATA),
								data, data, data, data),
						"Body", DirectionAndPosition.POSITION_CENTER);
			}
			case RESULT_MIN_DIS, RESULT_MAX_DIS -> {
				Object[] data = (Object[]) request.body.get(BodyCode.PAIR_POINTS);
				this.window.updateSection(body((Point[]) data[0],
						(PairPoint[]) data[1], // min NN
						(PairPoint[]) data[2], // max NN
						(PairPoint[]) data[3], // min NLogN
						(PairPoint[]) data[4] // max NLogN
				), "Body", DirectionAndPosition.POSITION_CENTER);
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
		this.window.addMenuBar(this.menu());
		this.window.addSection(this.header(), DirectionAndPosition.POSITION_TOP, "Header");
		PairPoint[] data = {};
		this.window.addSection(this.body(new Point[] {}, data, data, data, data),
				DirectionAndPosition.POSITION_CENTER, "Body");
		this.window.addSection(this.footer(), DirectionAndPosition.POSITION_BOTTOM, "Footer");
	}

	private JMenuBar menu() {
		JMenuBar menuBar = new JMenuBar();
		JMenu options = new JMenu("Opciones");
		JMenu stats = new JMenu("Estadisticas");
		JMenuItem alg = new JMenuItem("Algoritmos");
		// stats.setIcon(new ImageIcon(Config.ICON_TO_DISPLAY_MENU_OPTION));
		alg.addActionListener(e -> this.hub.notifyRequest(new Request<>(RequestCode.CALC_STATS, this)));
		JMenuItem jvm = new JMenuItem("JVM");
		// jvm.setIcon(new ImageIcon(Config.ICON_TO_DISPLAY_MENU_OPTION));
		jvm.addActionListener(e -> {
			WindowJVMStats jvmStats = new WindowJVMStats();
			jvmStats.show();
			jvmStats.start();
		});
		JMenuItem cleanData = new JMenuItem("Limpiar Datos");
		// cleanData.setIcon(new ImageIcon(Config.ICON_TO_DISPLAY_MENU_OPTION));
		cleanData.addActionListener(e -> this.hub.notifyRequest(new Request<>(RequestCode.CLEAR_DATA, this)));
		JMenuItem exit = new JMenuItem("Salir");
		// exit.setIcon(new ImageIcon(Config.ICON_TO_DISPLAY_MENU_OPTION));
		exit.addActionListener(e -> System.exit(0));
		stats.add(alg);
		stats.add(jvm);
		options.add(stats);
		options.addSeparator();
		options.add(cleanData);
		options.addSeparator();
		JMenu genData = new JMenu("Generar Datos");
		// genData.setIcon(new ImageIcon(Config.ICON_TO_DISPLAY_MENU_OPTION));
		String[] distributions = Arrays.stream(Distribution.values()).map(Enum::name).toArray(String[]::new);
		ButtonGroup grou = new ButtonGroup();
		for (String string : distributions) {
			JCheckBoxMenuItem item = new JCheckBoxMenuItem(string);
			if (string.equals(Distribution.UNIFORM.name())) {
				item.setSelected(true);
			}
			item.addActionListener(e -> {
				Distribution selectedValue = Distribution.valueOf(item.getText());
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
								.log(Level.SEVERE, "{0} is not implemented under the distribution menu.",
										selectedValue);
					}
				}
			});
			genData.add(item);
			grou.add(item);
		}

		options.add(genData);
		options.addSeparator();
		options.add(exit);
		menuBar.add(options);

		JMenu algorit = new JMenu("Algoritmos");
		// Create a checkbox menu item
		JCheckBoxMenuItem bruteForce = new JCheckBoxMenuItem("N^2");
		bruteForce.setSelected(true);
		bruteForce.addActionListener(e -> {
			this.hub.notifyRequest(new Request<>(RequestCode.CHANGE_ALGORITHM, this,
					new Body<>(RequestType.PUT, BodyCode.DATA, false)));
			this.hub.notifyRequest(new Request<>(RequestCode.CLEAR_SOLUTIONS, this));
		});
		JCheckBoxMenuItem divideAndConquer = new JCheckBoxMenuItem("NlogN");
		divideAndConquer.addActionListener(e -> {
			this.hub.notifyRequest(new Request<>(RequestCode.CHANGE_ALGORITHM, this,
					new Body<>(RequestType.PUT, BodyCode.DATA, true)));
			this.hub.notifyRequest(new Request<>(RequestCode.CLEAR_SOLUTIONS, this));
		});
		// Set that only one checkbox can be selected
		ButtonGroup group = new ButtonGroup();
		group.add(bruteForce);
		group.add(divideAndConquer);
		algorit.add(bruteForce);
		algorit.add(divideAndConquer);
		algorit.addSeparator();

		JCheckBoxMenuItem autoOnMax = new JCheckBoxMenuItem("Auto en Máx");
		autoOnMax.addActionListener(e -> {
			this.hub.notifyRequest(new Request<>(RequestCode.CHANGE_AUTO_MODE, this,
					new Body<>(RequestType.PUT, BodyCode.DATA, true)));
		});
		JCheckBoxMenuItem autoOnMin = new JCheckBoxMenuItem("Auto en Mín");
		autoOnMin.addActionListener(e -> {
			this.hub.notifyRequest(new Request<>(RequestCode.CHANGE_AUTO_MODE, this,
					new Body<>(RequestType.PUT, BodyCode.DATA, false)));
		});
		autoOnMin.setSelected(true);
		JCheckBoxMenuItem autoOnBenchMark = new JCheckBoxMenuItem("Auto en Benchmark");
		autoOnBenchMark.addActionListener(e -> {
			this.hub.notifyRequest(new Request<>(RequestCode.CHANGE_AUTO_MODE, this,
					new Body<>(RequestType.PUT, BodyCode.DATA, null)));
		});
		ButtonGroup group2 = new ButtonGroup();
		group2.add(autoOnMax);
		group2.add(autoOnMin);
		group2.add(autoOnBenchMark);
		algorit.add(autoOnMax);
		algorit.add(autoOnMin);
		algorit.add(autoOnBenchMark);

		menuBar.add(algorit);

		JMenu help = new JMenu("Ayuda");
		JMenuItem about = new JMenuItem("Manual de Usuario");
		// about.setIcon(new ImageIcon(Config.ICON_TO_DISPLAY_MENU_OPTION));
		about.addActionListener(e -> {
			// TODO
		});
		help.add(about);
		menuBar.add(help);

		return menuBar;
	}

	private Section footer() {
		Section buttonSection = new Section();
		JButton[] buttons = new JButton[3];
		buttons[0] = new JButton("Distancia Mínima");
		buttons[0].addActionListener(e -> this.hub.notifyRequest(new Request<>(RequestCode.CALC_MIN_DIS, this)));
		buttons[1] = new JButton("Dist Máxima");
		buttons[1].addActionListener(e -> this.hub.notifyRequest(new Request<>(RequestCode.CALC_MAX_DIS, this)));
		buttons[2] = new JButton("Auto");
		buttons[2].addActionListener(e -> {
			String txt = buttons[2].getText();
			if (txt.equals("Auto")) {
				buttons[2].setText("Stop");
				this.hub.notifyRequest(new Request<>(RequestCode.CALC_AUTO, this));
			} else {
				buttons[2].setText("Auto");
				this.hub.notifyRequest(new Request<>(RequestCode.STOP_AUTO, this));
			}
		});
		buttonSection.createButtons(buttons, DirectionAndPosition.DIRECTION_ROW);
		return buttonSection;
	}

	private Section body(Point[] data, PairPoint[] minPairPointsNN, PairPoint[] maxPairPointsNN,
			PairPoint[] minPairPointsNLogN, PairPoint[] maxPairPointsnLogN) {
		ScatterPlot scatterPlot = new ScatterPlot(Color.MAGENTA);
		JPanel content = new JPanel();
		content.setLayout(new BorderLayout());
		int width = this.hub.getModel().getFrameDimension().width;
		int height = this.hub.getModel().getFrameDimension().height;
		content.add(new ChartPanel(scatterPlot.createPlot(data, width, height,
				minPairPointsNN, maxPairPointsNN, minPairPointsNLogN, maxPairPointsnLogN)),
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
		content.setBackground(Color.WHITE);
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

		private JFreeChart createPlot(Point[] data, int width, int height, PairPoint[] minPairPointsNN,
				PairPoint[] maxPairPointsNN, PairPoint[] minPairPointsNLogN, PairPoint[] maxPairPointsNLogN) {
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
			renderer.setSeriesShape(0, new Ellipse2D.Double(-2, -2, 4, 4));
			plot.setRenderer(renderer);
			for (PairPoint pairPoint : minPairPointsNN) {
				XYLineAnnotation line = new XYLineAnnotation(
						pairPoint.p1().x(), pairPoint.p1().y(), // x and y coordinates of point 1
						pairPoint.p2().x(), pairPoint.p2().y(), // x and y coordinates of point 2
						new BasicStroke(1.0f),
						Color.BLUE);
				plot.addAnnotation(line);
			}
			for (PairPoint pairPoint : maxPairPointsNN) {
				XYLineAnnotation line = new XYLineAnnotation(
						pairPoint.p1().x(), pairPoint.p1().y(), // x and y coordinates of point 1
						pairPoint.p2().x(), pairPoint.p2().y(), // x and y coordinates of point 2
						new BasicStroke(1.0f),
						Color.RED);
				plot.addAnnotation(line);
			}
			for (PairPoint pairPoint : minPairPointsNLogN) {
				XYLineAnnotation line = new XYLineAnnotation(
						pairPoint.p1().x(), pairPoint.p1().y(), // x and y coordinates of point 1
						pairPoint.p2().x(), pairPoint.p2().y(), // x and y coordinates of point 2
						new BasicStroke(1.0f),
						Color.GREEN);
				plot.addAnnotation(line);
			}

			for (PairPoint pairPoint : maxPairPointsNLogN) {
				XYLineAnnotation line = new XYLineAnnotation(
						pairPoint.p1().x(), pairPoint.p1().y(), // x and y coordinates of point 1
						pairPoint.p2().x(), pairPoint.p2().y(), // x and y coordinates of point 2
						new BasicStroke(1.0f),
						Color.ORANGE);
				plot.addAnnotation(line);
			}
			plot.getDomainAxis().setRange(0, width);
			plot.getRangeAxis().setRange(0, height);
			chart.removeLegend();
			return chart;
		}
	}

}
