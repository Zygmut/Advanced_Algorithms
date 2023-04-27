package View;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;

import Services.Service;
import Services.Comunication.Content.Body;
import Services.Comunication.Request.Request;
import Services.Comunication.Request.RequestCode;
import Services.Comunication.Response.Response;
import betterSwing.Section;
import betterSwing.Window;
import betterSwing.utils.DirectionAndPosition;
import utils.Config;

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
		JButton button = new JButton("Hello World!");
		button.addActionListener(e -> {
			String message = "Hello World!";
			Request request = new Request(RequestCode.HELLO_WORLD, this, new Body(message));
			this.sendRequest(request);
		});
		sideBar.add(button);
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
		MapPlot scatterPlot = new MapPlot(Color.MAGENTA);
		JFreeChart chart = scatterPlot.createPlot();
		// TODO: Change this to be dynamic
		scatterPlot.changePlotBackground("./assets/images/pitiuses.png");
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// Get the mouse click coordinates
				int x = e.getX();
				int y = e.getY();
				// Calculate the data values based on the chart's range
				XYPlot plot = (XYPlot) chart.getPlot();
				ChartRenderingInfo info = chartPanel.getChartRenderingInfo();
				Rectangle2D dataArea = info.getPlotInfo().getDataArea();
				double xValue = plot.getDomainAxis().java2DToValue(x, dataArea, plot.getDomainAxisEdge());
				double yValue = plot.getRangeAxis().java2DToValue(y, dataArea, plot.getRangeAxisEdge());
				// TODO: Send the coordinates to the server
				// Print the clicked coordinates
				System.out.println("Clicked at: X=" + xValue + ", Y=" + yValue);
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
		JButton[] buttons = new JButton[3];
		buttons[0] = new JButton("Deshacer");
		buttons[0].addActionListener(e -> {
			// TODO
		});
		buttons[1] = new JButton("Rehacer");
		buttons[1].addActionListener(e -> {
			// TODO
		});
		buttons[2] = new JButton("Confirmar");
		buttons[2].addActionListener(e -> {
			// TODO
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

		private Color lineColor;
		private XYPlot plot;

		public MapPlot(Color lineColor) {
			this.lineColor = lineColor;
		}

		private JFreeChart createPlot() {
			JFreeChart chart = ChartFactory.createXYLineChart(
					"Mapa",
					"X",
					"Y",
					null);

			plot = chart.getXYPlot();
			plot.setBackgroundPaint(Color.WHITE);
			// TODO: Hide the axis
			// plot.getDomainAxis().setVisible(false);
			// plot.getRangeAxis().setVisible(false);
			XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(false, true);
			renderer.setSeriesPaint(0, this.lineColor);
			renderer.setSeriesShape(0, new Ellipse2D.Double(-2, -2, 4, 4));
			plot.setRenderer(renderer);
			plot.getDomainAxis().setRange(0, 100);
			plot.getRangeAxis().setRange(0, 100);

			return chart;
		}

		private void addData() {
			// TODO
		}

		private void changePlotBackground(String image) {
			plot.setBackgroundImage(Toolkit.getDefaultToolkit().getImage(image));
		}
	}
}
