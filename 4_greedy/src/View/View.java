package View;

import Model.*;
import Services.Comunication.Content.Body;
import Services.Comunication.Request.Request;
import Services.Comunication.Request.RequestCode;
import Services.Comunication.Response.Response;
import Services.Service;
import betterSwing.Section;
import betterSwing.Window;
import betterSwing.utils.DirectionAndPosition;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
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
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
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
import utils.Algorithms;
import utils.Maps;

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
	private ArrayList<GeoPoint> pointsSelected;
	private JButton[] buttons;
	private String selectedMap;
	private JTextArea textArea;
	private JComboBox<String> mapOptions;
	private GeoPoint lastPoint;

	/**
	 * This constructor creates a view with the MVC hub without any configuration
	 *
	 * @param mvc The MVC hub of the view.
	 */
	public View() {
		this.window = new Window();
		this.window.initConfig();
		this.pointsSelected = new ArrayList<>();
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
		this.pointsSelected = new ArrayList<>();
	}

	@Override
	public void start() {
		Logger
			.getLogger(this.getClass().getSimpleName())
			.log(Level.INFO, "View started.");
		this.loadContent();
		this.window.start();
	}

	@Override
	public void stop() {
		Logger
			.getLogger(this.getClass().getSimpleName())
			.log(Level.INFO, "View stopped.");
	}

	@Override
	public void notifyRequest(Request request) {
		switch (request.code) {
			case LOAD_MAP -> {
				Map map = (Map) request.body.content;
				this.scatterPlot.addMap(this.selectedMap, map);
			}
			case CHECK_GEOPOINT -> {
				String aux = textArea.getText();
				Logger
					.getLogger(this.getClass().getSimpleName())
					.log(Level.INFO, "Response [VIEW]: {0}", request);
				if (Objects.isNull(request.body.content)) {
					Logger
						.getLogger(this.getClass().getSimpleName())
						.log(Level.SEVERE, "Clicked point is not valid.");
					this.textArea.setText(aux + "\nClicked point is not valid.");
					return;
				}
				GeoPoint point = (GeoPoint) request.body.content;
				this.textArea.setText(aux + "\nClicked point is valid.\n  =>" + point.toString());
				if (!this.pointsSelected.contains(point)){
			    	this.scatterPlot.addSelectPoint(point);
					this.pointsSelected.add(point);
				} else {
					Logger
						.getLogger(this.getClass().getSimpleName())
						.log(Level.SEVERE, "Clicked point is already selected.");
					this.textArea.setText(aux + "\nClicked point is already selected.");
				}
			}
			default -> {
				Logger
					.getLogger(this.getClass().getSimpleName())
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
		this.initSplitPane();
		this.splitPane.setRightComponent(this.sideBar());
		this.window.addSection(
				this.body(),
				DirectionAndPosition.POSITION_CENTER,
				"Body"
			);
		this.window.addSection(
				this.footer(),
				DirectionAndPosition.POSITION_BOTTOM,
				"Footer"
			);
		this.mapOptions.getActionListeners()[0].actionPerformed(
				new ActionEvent(
					this,
					ActionEvent.ACTION_PERFORMED,
					this.selectedMap
				)
			);
	}

	private JPanel sideBar() {
		JPanel sideBar = new JPanel();
		sideBar.setBackground(Color.WHITE);
		sideBar.setLayout(new GridLayout(2, 1));
		JPanel actionsPanel = new JPanel();
		actionsPanel.setBackground(Color.WHITE);
		actionsPanel.setLayout(new BoxLayout(actionsPanel, BoxLayout.Y_AXIS));
		JPanel mapSelectorPanel = new JPanel();
		mapSelectorPanel.setBackground(Color.WHITE);
		JLabel mapSelectorLabel = new JLabel("Mapa: ");
		String[] maps = Arrays
			.stream(Maps.values())
			.map(Enum::toString)
			.toArray(String[]::new);
		this.mapOptions = new JComboBox<>(maps);
		this.selectedMap = Maps.IBIZA_FORMENTERA.toString();
		mapOptions.setSelectedItem(this.selectedMap);
		mapOptions.addActionListener(e -> {
			String map = (String) mapOptions.getSelectedItem();
			String[] mapsNames = Maps.getMaps();
			int index = Arrays.asList(mapsNames).indexOf(map);
			System.out.println(Maps.values()[index]);
			Body body = new Body(Maps.values()[index].toString());
			Request request = new Request(RequestCode.PARSE_MAP, this, body);
			this.sendRequest(request);
		});
		mapSelectorPanel.add(mapSelectorLabel);
		mapSelectorPanel.add(mapOptions);
		actionsPanel.add(mapSelectorPanel);

		JPanel algSelectorPanel = new JPanel();
		algSelectorPanel.setBackground(Color.WHITE);
		JLabel algSelectorLabel = new JLabel("Algoritmo: ");
		String[] algorithms = Arrays
			.stream(Algorithms.values())
			.map(Enum::toString)
			.toArray(String[]::new);
		JComboBox<String> algorithmMenu = new JComboBox<>(algorithms);
		algorithmMenu.addActionListener(e -> {
			String algorithm = (String) algorithmMenu.getSelectedItem();
			System.out.println(algorithm);
		});
		algSelectorPanel.add(algSelectorLabel);
		algSelectorPanel.add(algorithmMenu);
		actionsPanel.add(algSelectorPanel);
		sideBar.add(actionsPanel);

		JPanel infoPanel = new JPanel();
		infoPanel.setBackground(Color.WHITE);
		infoPanel.setLayout(new BorderLayout());
		this.textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setText("Logs: \n");

		// Wrap a scrollpane around it.
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setVerticalScrollBarPolicy(
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS
		);
		infoPanel.add(scrollPane, BorderLayout.CENTER);
		sideBar.add(infoPanel);

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
		this.scatterPlot =
			new MapPlot(Color.MAGENTA, Color.BLACK, Color.PINK, true);

		JFreeChart chart = this.scatterPlot.createPlot();
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setDomainZoomable(false);
		chartPanel.setRangeZoomable(false);
		chartPanel.addMouseListener(
			new MouseListener() {
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
						double xValue = plot
							.getDomainAxis()
							.java2DToValue(
								x,
								dataArea,
								plot.getDomainAxisEdge()
							);
						double yValue = plot
							.getRangeAxis()
							.java2DToValue(
								y,
								dataArea,
								plot.getRangeAxisEdge()
							);
						System.out.println(
							"Clicked at: X=" + xValue + ", Y=" + yValue
						);
						GeoPoint point = new GeoPoint(xValue, yValue);
						Body body = new Body(point);
						Request request = new Request(
							RequestCode.CHECK_GEOPOINT,
							View.this,
							body
						);
						View.this.sendRequest(request);
					}
				}

				// Implement the remaining MouseListener methods
				@Override
				public void mousePressed(MouseEvent e) {}

				@Override
				public void mouseReleased(MouseEvent e) {}

				@Override
				public void mouseEntered(MouseEvent e) {}

				@Override
				public void mouseExited(MouseEvent e) {}
			}
		);
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
				if (!this.pointsSelected.isEmpty()) {
					lastPoint = this.pointsSelected.get(
						this.pointsSelected.size() - 1
					);
				}
				this.pointsSelected.remove(this.pointsSelected.size() - 1);
				this.scatterPlot.removeLastPoint();
			});
		buttons[1] = new JButton("Rehacer");
		buttons[1].addActionListener(e -> {
			if (lastPoint != null)
				this.pointsSelected.add(lastPoint);
				this.scatterPlot.restoreLastPoint();
				lastPoint = null;
			});
		buttons[2] = new JButton("Confirmar");
		buttons[2].addActionListener(e -> {
				// TODO: Send info to server
				Body body = new Body(this.pointsSelected); // Geopoints might be Serializable
				Request request = new Request(
					RequestCode.SEND_GEOPOINTS,
					this,
					body
				);
				View.this.sendRequest(request);
				this.pointsSelected = new ArrayList<>();
			});
		buttonSection.createButtons(
			buttons,
			DirectionAndPosition.DIRECTION_ROW
		);
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
		private XYSeries nodesPoint;
		private boolean enableDistanceDisplay;

		public MapPlot(
			Color mapNodesColor,
			Color selectPointColor,
			Color nodeLinesColor,
			boolean enableDistanceDisplay
		) {
			this.mapNodesColor = mapNodesColor;
			this.selectPointColor = selectPointColor;
			this.nodeLinesColor = nodeLinesColor;
			this.enableDistanceDisplay = enableDistanceDisplay;
		}

		private JFreeChart createPlot() {
			XYSeriesCollection dataset = new XYSeriesCollection();
			this.selectedPoint = new XYSeries("selectedPoint");
			this.nodesPoint = new XYSeries("nodesPoint");
			dataset.addSeries(this.selectedPoint);
			dataset.addSeries(this.nodesPoint);

			JFreeChart chart = ChartFactory.createXYLineChart(
				"",
				"X",
				"Y",
				dataset
			);
			chart.setAntiAlias(true);
			plot = chart.getXYPlot();
			plot.setBackgroundPaint(Color.WHITE);
			plot.getDomainAxis().setVisible(false);
			plot.getRangeAxis().setVisible(false);
			XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(
				false,
				true
			);
			renderer.setSeriesPaint(1, this.mapNodesColor);
			renderer.setSeriesShape(1, new Ellipse2D.Double(-4, -4, 8, 8));
			renderer.setSeriesPaint(0, this.selectPointColor);
			renderer.setSeriesShape(0, new Ellipse2D.Double(-4, -4, 8, 8));
			plot.setRenderer(renderer);

			plot.getDomainAxis().setRange(0, 100);
			plot.getRangeAxis().setRange(0, 100);
			chart.removeLegend();

			return chart;
		}

		private void addMap(String selectedMap, Map map) {
			String backImgPath =
				"./assets/" + selectedMap.toLowerCase() + "/" + map.img();
			Node[] nodes = map.graph().content();

			GeoPoint[] points = new GeoPoint[nodes.length];
			for (int i = 0; i < points.length; i++) {
				points[i] = nodes[i].geoPoint();
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

			changePlotBackground(backImgPath);

			for (PairPoint pairPoint : lines) {
				XYLineAnnotation line = new XYLineAnnotation(
					pairPoint.p1().x(),
					pairPoint.p1().y(), // x and y coordinates of point 1
					pairPoint.p2().x(),
					pairPoint.p2().y(), // x and y coordinates of point 2
					new BasicStroke(1.0f),
					this.nodeLinesColor
				);
				plot.addAnnotation(line);
				if (!this.enableDistanceDisplay) {
					continue;
				}
				// Create a text annotation
				double distance = pairPoint
					.p1()
					.euclideanDistanceTo(pairPoint.p2());
				// Round to 2 decimals
				distance = Math.round(distance * 100.0) / 100.0;
				XYTextAnnotation textAnnotation = new XYTextAnnotation(
					distance + " U", // Text to be displayed
					(pairPoint.p1().x() + pairPoint.p2().x()) / 2, // x coordinate of text
					(pairPoint.p1().y() + pairPoint.p2().y()) / 2 // y coordinate of text
				);
				plot.addAnnotation(textAnnotation);
			}

			for (GeoPoint point : points) {
				this.nodesPoint.add(point.x(), point.y());
			}
		}

		private void addSelectPoint(GeoPoint point) {
			String text = "" + View.this.pointsSelected.size();
			XYTextAnnotation textAnnotation = new XYTextAnnotation(
				text, // Text to be displayed
				point.x(), // x coordinate of text
				point.y() + 2 // y coordinate of text
			);
			plot.addAnnotation(textAnnotation);
			this.selectedPoint.add(point.x(), point.y());
		}

		private void removeLastPoint() {
			if (this.selectedPoint.getItemCount() > 0) {
				this.selectedPoint.remove(
						this.selectedPoint.getItemCount() - 1
					);
			}
		}

		private int getSelectedPointsCount() {
			return this.selectedPoint.getItemCount();
		}

		//Method that restores the last point that was removed
		private void restoreLastPoint() {
			this.selectedPoint.add(lastPoint.x(), lastPoint.y());
		}

		private void changePlotBackground(String image) {
			plot.setBackgroundImage(
				Toolkit.getDefaultToolkit().getImage(image)
			);
			plot.setBackgroundImageAlpha(1);
		}
	}
}
