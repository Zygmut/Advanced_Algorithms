package View;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.BasicStroke;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYLineAnnotation;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.google.gson.Gson;

import Services.Service;
import Services.Comunication.Content.Body;
import Services.Comunication.Request.Request;
import Services.Comunication.Request.RequestCode;
import Services.Comunication.Response.Response;
import betterSwing.Section;
import betterSwing.Window;
import betterSwing.utils.DirectionAndPosition;
import utils.Algorithms;
import utils.Config;
import utils.Maps;
import Model.*;

public class View implements Service {

	/**
	 * The window of the view.
	 */
	private Window window;
	/**
	 * The split pane of the view.
	 */
	private JSplitPane splitPane;
	/**
	 * Map plot of the view.
	 */
	private MapPlot scatterPlot;
	private int numberOfPointsSelected = 0;
	private JButton[] buttons;

	/**
	 * This constructor creates a view with the MVC hub without any configuration
	 *
	 * @param mvc The MVC hub of the view.
	 */
	public View() {
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
	 */
	public View(String configPath) {
		this.window = new Window(configPath);
		this.window.initConfig();
	}

	@Override
	public void start() {
		Logger.getLogger(this.getClass().getSimpleName())
				.log(Level.INFO, "View started.");
		this.loadContent();
		this.window.start();
	}

	@Override
	public void stop() {
		Logger.getLogger(this.getClass().getSimpleName())
				.log(Level.INFO, "View stopped.");
	}

	@Override
	public void notifyRequest(Request request) {
		switch (request.code) {
			default -> {
				Logger.getLogger(this.getClass().getSimpleName())
						.log(Level.SEVERE, "{0} is not implemented.", request);
			}
		}
	}

	@Override
	public void sendRequest(Request request) {
		try (Socket socket = new Socket(Config.SERVER_HOST, Config.SERVER_PORT)) {
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			out.writeObject(request);

			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			Response response = (Response) in.readObject();
			Logger.getLogger(this.getClass().getSimpleName())
					.log(Level.INFO, "Response: {0}", response);
		} catch (Exception e) {
			Logger.getLogger(this.getClass().getSimpleName())
					.log(Level.SEVERE, "Error while sending request.", e);
		}
	}

	@Override
	public void sendResponse(Response response) {
		try (Socket socket = new Socket(Config.SERVER_HOST, Config.SERVER_PORT)) {
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			out.writeObject(response);
		} catch (Exception e) {
			Logger.getLogger(this.getClass().getSimpleName())
					.log(Level.SEVERE, "Error while sending response.", e);
		}
	}

	/**
	 * Loads all the view content.
	 *
	 */
	private void loadContent() {
		this.window.addMenuBar(this.menu());
		this.initSplitPane();
		this.splitPane.setRightComponent(this.sideBar());
		this.window.addSection(this.body(), DirectionAndPosition.POSITION_CENTER, "Body");
		this.window.addSection(this.footer(), DirectionAndPosition.POSITION_BOTTOM, "Footer");
	}

	private JPanel sideBar() {
		JPanel sideBar = new JPanel();
		sideBar.setBackground(Color.WHITE);
		sideBar.setLayout(new BoxLayout(sideBar, BoxLayout.Y_AXIS));
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBackground(Color.WHITE);
		JLabel label = new JLabel("Demo: ");
		JButton button = new JButton("Hello World!");
		button.addActionListener(e -> {
			String message = "Hello World!";
			Request request = new Request(RequestCode.HELLO_WORLD, this, new Body(message));
			this.sendRequest(request);
		});
		buttonPanel.add(label);
		buttonPanel.add(button);
		sideBar.add(buttonPanel);
		JPanel mapSelectorPanel = new JPanel();
		mapSelectorPanel.setBackground(Color.WHITE);
		JLabel mapSelectorLabel = new JLabel("Mapa: ");
		String[] maps = Arrays.stream(Maps.values()).map(Enum::toString).toArray(String[]::new);
		JComboBox<String> distributionMenu = new JComboBox<>(maps);
		distributionMenu.addActionListener(e -> {
			String map = (String) distributionMenu.getSelectedItem();
			String[] mapsNames = Maps.getMaps();
			int index = Arrays.asList(mapsNames).indexOf(map);
			System.out.println(Maps.values()[index]);
			// TODO: Send new map to server
			// TODO: Update map
		});
		mapSelectorPanel.add(mapSelectorLabel);
		mapSelectorPanel.add(distributionMenu);
		sideBar.add(mapSelectorPanel);

		JPanel algSelectorPanel = new JPanel();
		algSelectorPanel.setBackground(Color.WHITE);
		JLabel algSelectorLabel = new JLabel("Algoritmo: ");
		String[] algorithms = Arrays.stream(Algorithms.values()).map(Enum::toString).toArray(String[]::new);
		JComboBox<String> algorithmMenu = new JComboBox<>(algorithms);
		algorithmMenu.addActionListener(e -> {
			String algorithm = (String) algorithmMenu.getSelectedItem();
			System.out.println(algorithm);
		});
		algSelectorPanel.add(algSelectorLabel);
		algSelectorPanel.add(algorithmMenu);
		sideBar.add(algSelectorPanel);

		return sideBar;
	}

	private void initSplitPane() {
		this.splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		this.splitPane.setDividerLocation(0.9);
		this.splitPane.setResizeWeight(1.0);
		this.splitPane.setOneTouchExpandable(true);
		this.splitPane.setBorder(null);
	}

	private Section body() {
		JPanel content = new JPanel();
		content.setLayout(new BorderLayout());
		content.setBackground(Color.WHITE);
		this.scatterPlot = new MapPlot(Color.MAGENTA, Color.BLACK, Color.PINK, true);
		JFreeChart chart = this.scatterPlot.createPlot("./assets/ibiza-formentera/map.png");
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setDomainZoomable(false);
		chartPanel.setRangeZoomable(false);
		chartPanel.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e)) {
					// Get the mouse click coordinates
					int x = e.getX();
					int y = e.getY();
					// Calculate the data values based on the chart's range
					XYPlot plot = (XYPlot) chart.getPlot();
					ChartRenderingInfo info = chartPanel.getChartRenderingInfo();
					Rectangle2D dataArea = info.getPlotInfo().getDataArea();
					double xValue = plot.getDomainAxis().java2DToValue(x, dataArea, plot.getDomainAxisEdge());
					double yValue = plot.getRangeAxis().java2DToValue(y, dataArea, plot.getRangeAxisEdge());
					System.out.println("Clicked at: X=" + xValue + ", Y=" + yValue);
					View.this.numberOfPointsSelected++;
					scatterPlot.addSelectPoint(new GeoPoint(xValue, yValue));
				}
			}

			// Implement the remaining MouseListener methods
			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}
		});
		content.add(chartPanel, BorderLayout.CENTER);
		splitPane.setLeftComponent(content);
		Section body = new Section();
		body.createJSplitPaneSection(splitPane);
		return body;
	}

	private Section footer() {
		Section buttonSection = new Section();
		this.buttons = new JButton[3];
		buttons[0] = new JButton("Deshacer");
		buttons[0].addActionListener(e -> {
			this.numberOfPointsSelected--;
			this.scatterPlot.removeLastPoint();
		});
		buttons[1] = new JButton("Rehacer");
		buttons[1].addActionListener(e -> {
			this.numberOfPointsSelected++;
			this.scatterPlot.restoreLastPoint();
		});
		buttons[2] = new JButton("Confirmar");
		buttons[2].addActionListener(e -> {
			this.numberOfPointsSelected = 0;
			// TODO: Send info to server
			Request request = new Request(RequestCode.HELLO_WORLD, this);
			this.sendRequest(request);
		});
		buttonSection.createButtons(buttons, DirectionAndPosition.DIRECTION_ROW);
		return buttonSection;
	}

	/**
	 * This method creates the menu bar of the view.
	 *
	 * @return JMenuBar The menu bar of the view.
	 */
	private JMenuBar menu() {
		JMenuBar menuBar = new JMenuBar();
		JMenu options = new JMenu("Opciones");
		JMenu stats = new JMenu("Estadisticas");
		JMenuItem alg = new JMenuItem("Algoritmos");
		alg.addActionListener(e -> {
			// TODO
		});
		JMenuItem jvm = new JMenuItem("JVM");
		jvm.addActionListener(e -> {
			WindowJVMStats jvmStats = new WindowJVMStats();
			jvmStats.show();
			jvmStats.start();
		});
		JMenuItem exit = new JMenuItem("Salir");
		exit.addActionListener(e -> System.exit(0));
		stats.add(alg);
		stats.add(jvm);
		options.add(stats);

		options.addSeparator();
		options.add(exit);
		menuBar.add(options);

		JMenu help = new JMenu("Ayuda");
		JMenuItem about = new JMenuItem("Manual de Usuario");
		about.addActionListener(e -> {
			WindowUsage usage = new WindowUsage();
			usage.show();
		});
		help.add(about);
		menuBar.add(help);

		return menuBar;
	}

	private class MapPlot {

		private Color mapNodesColor;
		private Color selectPointColor;
		private Color nodeLinesColor;
		private XYPlot plot;
		private XYSeries selectedPoint;
		private boolean enableDistanceDisplay;

		public MapPlot(Color mapNodesColor, Color selectPointColor, Color nodeLinesColor,
				boolean enableDistanceDisplay) {
			this.mapNodesColor = mapNodesColor;
			this.selectPointColor = selectPointColor;
			this.nodeLinesColor = nodeLinesColor;
			this.enableDistanceDisplay = enableDistanceDisplay;
		}

		private JFreeChart createPlot(String backImgPath) {
			// TODO: REMOVE THIS IS DEBU
			Gson gson = new Gson();
			Map map = null;
			try (Reader reader = new FileReader("./assets/ibiza-formentera/ibiza-formentera.json")) {
				// Convert JSON File to Java Object
				map = gson.fromJson(reader, Map.class);
			} catch (IOException e) {
				System.out.println(e.getLocalizedMessage());
			}

			Node[] nodes = map.graph().content();

			GeoPoint[] points = new GeoPoint[nodes.length];
			for (int i = 0; i < points.length; i++) {
				points[i] = nodes[i].geoPoint();
			}

			XYSeries series = new XYSeries("MapPoints");
			for (GeoPoint point : points) {
				series.add(point.x(), point.y());
			}

			ArrayList<PairPoint> lines = new ArrayList<>();
			for (Node node : nodes) {
				GeoPoint originPoint = node.geoPoint();
				for (Connection connection : node.connections()) {
					String nodeId = connection.nodeId();
					for (Node target : nodes) {
						if (target.id().equals(nodeId)) {
							GeoPoint targetPoint = target.geoPoint();
							lines.add(new PairPoint(originPoint, targetPoint));
							break;
						}
					}
				}
			}

			XYSeriesCollection dataset = new XYSeriesCollection();
			this.selectedPoint = new XYSeries("selectedPoint");
			dataset.addSeries(this.selectedPoint);
			dataset.addSeries(series);
			// END DEBUG

			JFreeChart chart = ChartFactory.createXYLineChart(
					"",
					"X",
					"Y",
					dataset);

			chart.setAntiAlias(true);
			plot = chart.getXYPlot();
			changePlotBackground(backImgPath);
			plot.setBackgroundPaint(Color.WHITE);
			plot.getDomainAxis().setVisible(false);
			plot.getRangeAxis().setVisible(false);
			XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(false, true);
			renderer.setSeriesPaint(1, this.mapNodesColor);
			renderer.setSeriesShape(1, new Ellipse2D.Double(-4, -4, 8, 8));
			renderer.setSeriesPaint(0, this.selectPointColor);
			renderer.setSeriesShape(0, new Ellipse2D.Double(-4, -4, 8, 8));
			plot.setRenderer(renderer);

			for (PairPoint pairPoint : lines) {
				XYLineAnnotation line = new XYLineAnnotation(
						pairPoint.p1().x(), pairPoint.p1().y(), // x and y coordinates of point 1
						pairPoint.p2().x(), pairPoint.p2().y(), // x and y coordinates of point 2
						new BasicStroke(1.0f),
						this.nodeLinesColor);
				plot.addAnnotation(line);
				if (!this.enableDistanceDisplay) {
					continue;
				}
				// Create a text annotation
				double distance = pairPoint.p1().euclideanDistanceTo(pairPoint.p2());
				// Round to 2 decimals
				distance = Math.round(distance * 100.0) / 100.0;
				XYTextAnnotation textAnnotation = new XYTextAnnotation(
						distance + " U", // Text to be displayed
						(pairPoint.p1().x() + pairPoint.p2().x()) / 2, // x coordinate of text
						(pairPoint.p1().y() + pairPoint.p2().y()) / 2 // y coordinate of text
				);
				plot.addAnnotation(textAnnotation);
			}
			plot.getDomainAxis().setRange(0, 100);
			plot.getRangeAxis().setRange(0, 100);
			chart.removeLegend();

			return chart;
		}

		private void addSelectPoint(GeoPoint point) {
			this.selectedPoint.add(point.x(), point.y());
		}

		private void removeLastPoint() {
			if (this.selectedPoint.getItemCount() > 0) {
				this.selectedPoint.remove(this.selectedPoint.getItemCount() - 1);
			}
		}

		private int getSelectedPointsCount() {
			return this.selectedPoint.getItemCount();
		}

		private void restoreLastPoint() {
			// TODO
		}

		private void changePlotBackground(String image) {
			plot.setBackgroundImage(Toolkit.getDefaultToolkit().getImage(image));
			plot.setBackgroundImageAlpha(1);
		}
	}
}
