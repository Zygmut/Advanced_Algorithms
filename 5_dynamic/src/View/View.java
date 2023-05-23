package View;

import Services.Comunication.Content.Body;
import Services.Comunication.Request.Request;
import Services.Comunication.Request.RequestCode;
import Services.Service;

import betterSwing.Section;
import betterSwing.Window;
import betterSwing.utils.DirectionAndPosition;
import utils.Config;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

import Model.ExecResultData;
import Model.ExecResultDataTreeNode;
import Model.Language;

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
	 * The list of buttons of the view.
	 */
	private JButton[] buttons;
	/**
	 * The text area of the view to display logs.
	 */
	private JTextArea textArea;
	/**
	 * The list of dictionaries of the view.
	 */
	private Map<String, Integer> optionsAnalysisMode;
	/**
	 * The list of dictionaries of the view.
	 */
	private List<String> optionsDictionary;
	/**
	 *
	 */
	private JPanel[] bodyScreens;
	/**
	 *
	 */
	private int currentBodyScreenIndex;

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
			case GET_LANG_NAMES -> {
				String[] names = (String[]) request.body.content;
				Logger.getLogger(this.getClass().getSimpleName())
						.log(Level.INFO, "Data Base languages are: {0}.", Arrays.deepToString(names));
			}
			case ADD_RESULT -> {
				Object[] result = (Object[]) request.body.content;

				ExecResultData[] graphData = (ExecResultData[]) result[1];
				ExecResultDataTreeNode treeData = (ExecResultDataTreeNode) result[2];

				// Arrows
				this.buttons[0].setEnabled(true);
				this.buttons[1].setEnabled(true);

				// Init instances
				DistanceGraph distanceGraph = new DistanceGraph(Color.BLACK, Color.LIGHT_GRAY);
				BarChartPlot barChartPlot = new BarChartPlot();
				LexicalTree lexicalTree = new LexicalTree(Color.BLACK, Color.WHITE);

				this.bodyScreens[1] = distanceGraph.createGraph(graphData, "Grafo de distancias");
				JPanel panel = new JPanel();
				panel.setLayout(new GridLayout(4, 3));
				for (ExecResultData res : graphData) {
					panel.add(barChartPlot.createBarChartPlot(res.connections(), res.id(), null, null));
				}
				final int totalPanels = 12;
				for (int i = 0; i < totalPanels - graphData.length; i++) {
					JPanel emptyPanel = new JPanel();
					emptyPanel.setBackground(Color.WHITE);
					panel.add(emptyPanel);
				}
				this.bodyScreens[2] = panel;
				this.bodyScreens[3] = lexicalTree.createTree(treeData, "Árbol lexico");

				// "Pagination"
				this.splitPane.setLeftComponent(this.bodyScreens[1]);
				this.currentBodyScreenIndex = 1;
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
		this.initSplitPane();
		this.splitPane.setRightComponent(this.sideBar());
		this.window.addSection(
				this.body(),
				DirectionAndPosition.POSITION_CENTER,
				"Body");
		this.window.addSection(
				this.footer(),
				DirectionAndPosition.POSITION_BOTTOM,
				"Footer");
	}

	private JPanel sideBar() {
		JPanel sideBar = new JPanel();
		sideBar.setBackground(Color.WHITE);
		sideBar.setLayout(new GridLayout(3, 1));

		// Logo panel
		JPanel logoPanel = new JPanel();
		logoPanel.setBackground(Color.WHITE);
		JPanel iconPanel = new JPanel();
		iconPanel.setBackground(Color.WHITE);
		JLabel icon = new JLabel();
		icon.setBackground(Color.WHITE);
		icon.setIcon(this.escalateImageIcon(Config.APP_UI_ICON_PATH, 128, 128));
		iconPanel.add(icon);
		logoPanel.add(iconPanel);
		sideBar.add(logoPanel);

		// Actions panel
		JPanel actionsPanel = new JPanel();
		actionsPanel.setBackground(Color.WHITE);
		actionsPanel.setLayout(new BoxLayout(actionsPanel, BoxLayout.Y_AXIS));
		actionsPanel.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));

		JLabel analysisMode = new JLabel("Tamaño de comparación: ");
		this.optionsAnalysisMode = new HashMap<>();

		analysisMode.setFont(new Font("Arial", Font.ITALIC, 14));
		JSpinner statsMode = new JSpinner(new SpinnerNumberModel(1000, 1, Integer.MAX_VALUE, 1));
		this.optionsAnalysisMode.put("batchSize", (int) statsMode.getValue());
		statsMode.addChangeListener(e -> {
			this.optionsAnalysisMode.put("batchSize", (int) statsMode.getValue());
		});

		JCheckBox parallel = new JCheckBox("Paralelizar");
		parallel.setSelected(true);
		this.optionsAnalysisMode.put("parallel", 1);
		parallel.addActionListener(e -> {
			final Integer val = parallel.isSelected() ? 1 : 0;
			this.optionsAnalysisMode.put("parallel", val);
		});

		actionsPanel.add(Box.createVerticalStrut(10));
		actionsPanel.add(analysisMode);
		actionsPanel.add(Box.createVerticalStrut(5));
		actionsPanel.add(statsMode);
		actionsPanel.add(Box.createVerticalStrut(5));
		actionsPanel.add(parallel);
		actionsPanel.add(Box.createVerticalStrut(5));

		sideBar.add(actionsPanel);

		// Log panel
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
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		infoPanel.add(scrollPane, BorderLayout.CENTER);
		sideBar.add(infoPanel);

		return sideBar;
	}

	private ImageIcon escalateImageIcon(String iconPath, int width, int height) {
		Image image = new ImageIcon(iconPath).getImage();
		return new ImageIcon(image.getScaledInstance(width, height, Image.SCALE_SMOOTH));
	}

	private void initSplitPane() {
		this.splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		this.splitPane.setDividerLocation(0.9);
		this.splitPane.setResizeWeight(1.0);
		this.splitPane.setOneTouchExpandable(true);
		this.splitPane.setBorder(null);
	}

	private Section body() {
		this.bodyScreens = new JPanel[4];

		JPanel content = new JPanel();
		content.setLayout(new BorderLayout());
		content.setBackground(Color.WHITE);

		String[] dictLanguages = Arrays.stream(Language.values())
				.map(Language::name)
				.toArray(String[]::new);

		JPanel dictPanel = new JPanel();
		dictPanel.setBackground(Color.WHITE);
		dictPanel.setLayout(new GridLayout(4, 3));

		this.optionsDictionary = new ArrayList<>();
		for (int i = 0; i < dictLanguages.length; i++) {
			JButton dict = new JButton(dictLanguages[i]);
			dict.setBackground(Color.LIGHT_GRAY);

			// Leer el icono de la bandera hace que el programa se ejecute más lento
			final String pathToIcon = Config.ICON_FLAGS_PATH + dictLanguages[i].toLowerCase() + ".png";
			dict.setIcon(this.escalateImageIcon(pathToIcon, 32, 32));
			Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Icono de la bandera cargado: {0}", pathToIcon);

			dict.addActionListener(e -> {
				dict.setSelected(!dict.isSelected());
				if (dict.isSelected()) {
					dict.setBackground(Color.LIGHT_GRAY.darker());
					this.optionsDictionary.add(dict.getText());
				} else {
					this.optionsDictionary.remove(dict.getText());
					dict.setBackground(Color.LIGHT_GRAY);
				}
				Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Diccionarios seleccionados: {0}",
						this.optionsDictionary);
				String displayText = this.optionsDictionary.isEmpty() ? "Ninguno" : this.optionsDictionary.toString();
				this.textArea.append("Diccionarios seleccionados: " + displayText + "\n");
			});
			if (i == dictLanguages.length - 1) {
				JPanel spacingPanel = new JPanel();
				spacingPanel.setBackground(Color.WHITE);
				dictPanel.add(spacingPanel);
			}
			dictPanel.add(dict);
		}
		content.add(dictPanel, BorderLayout.CENTER);

		this.bodyScreens[0] = content;
		splitPane.setLeftComponent(content);
		Section body = new Section();
		body.createJSplitPaneSection(splitPane);
		return body;
	}

	private Section footer() {
		Section buttonSection = new Section();
		this.buttons = new JButton[3];

		this.buttons[0] = new JButton("<");
		this.buttons[0].setEnabled(false);
		this.buttons[0].addActionListener(e -> {
			if (this.currentBodyScreenIndex > 0) {
				this.currentBodyScreenIndex--;
				this.splitPane.setLeftComponent(this.bodyScreens[this.currentBodyScreenIndex]);
			}
		});

		this.buttons[1] = new JButton(">");
		this.buttons[1].setEnabled(false);
		this.buttons[1].addActionListener(e -> {
			if (this.currentBodyScreenIndex < this.bodyScreens.length - 1) {
				this.currentBodyScreenIndex++;
				this.splitPane.setLeftComponent(this.bodyScreens[this.currentBodyScreenIndex]);
			}
		});

		this.buttons[2] = new JButton("Inciar");
		this.buttons[2].addActionListener(e -> {
			Body body = new Body(new Object[] { this.optionsDictionary.toArray(String[]::new), optionsAnalysisMode });
			this.sendRequest(new Request(RequestCode.LEVENSHTEIN, this, body));
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
			// TODO TIMED EXECTUION GRAPH
		});
		JMenuItem jvm = new JMenuItem("JVM");
		jvm.addActionListener(e -> {
			WindowJVMStats jvmStats = new WindowJVMStats();
			jvmStats.show();
			jvmStats.start();
		});
		JMenuItem wordGuesser = new JMenuItem("Adivinador de palabras");
		wordGuesser.addActionListener(e -> {
			// TODO
			WindowWordGuesser wordGuesserWindow = new WindowWordGuesser();
			wordGuesserWindow.show();
		});

		JMenuItem reiniciar = new JMenuItem("Reiniciar");
		reiniciar.addActionListener(e -> {
			this.window.updateSection(body(), "Body", DirectionAndPosition.POSITION_CENTER);

		});
		JMenuItem exit = new JMenuItem("Salir");
		exit.addActionListener(e -> System.exit(0));
		stats.add(alg);
		stats.add(jvm);
		options.add(stats);
		options.add(reiniciar);
		options.add(wordGuesser);

		options.addSeparator();
		options.add(exit);
		menuBar.add(options);

		JMenu db = new JMenu("Base de Datos");
		JMenuItem load = new JMenuItem("Cargar");
		load.addActionListener(e -> {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnValue = fileChooser.showOpenDialog(null);
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				File selectedDirectory = fileChooser.getSelectedFile();
				Body body = new Body(selectedDirectory.getAbsolutePath());
				Request request = new Request(RequestCode.LOAD_DB, this, body);
				this.sendRequest(request);
			}

		});
		JMenuItem getNames = new JMenuItem("Obtener Nombres");
		getNames.addActionListener(e -> {
			Request request = new Request(RequestCode.GET_LANG_NAMES, this);
			this.sendRequest(request);
		});
		db.add(load);
		db.add(getNames);
		menuBar.add(db);

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

	private String toHex(Color color) {
		return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
	}

	private class DistanceGraph {

		private Color connectionLineColor;
		private Color nodesColor;

		public DistanceGraph(Color connectionLineColor, Color nodesColor) {
			this.connectionLineColor = connectionLineColor;
			this.nodesColor = nodesColor;
		}

		private JPanel createGraph(ExecResultData[] data, String title) {
			// Create a new graph
			mxGraph graph = new mxGraph();

			// Get the default parent for the graph
			Object parent = graph.getDefaultParent();

			// Begin a new transaction
			graph.getModel().beginUpdate();
			try {
				Map<String, Object> map = new HashMap<>();
				for (ExecResultData execResultData : data) {
					int x = (int) (Math.random() * View.this.bodyScreens[0].getWidth());
					int y = (int) (Math.random() * View.this.bodyScreens[0].getHeight());
					Object v1 = graph.insertVertex(parent, null, execResultData.id(), x, y, 80, 30,
							"fillColor=" + toHex(this.nodesColor));
					map.put(execResultData.id(), v1);
				}

				for (ExecResultData execResultData : data) {
					for (ExecResultData.Connection con : execResultData.connections()) {
						final double value = Math.round(con.value() * 100.0) / 100.0;
						graph.insertEdge(parent, null, value + "", map.get(execResultData.id()), map.get(con.id()));
					}
				}

				graph.setCellsEditable(false);
				graph.setEdgeLabelsMovable(false);
				graph.setCellsCloneable(false);
				graph.setCellsDeletable(false);
				graph.setCellsDisconnectable(false);
				graph.setCellsBendable(false);
				graph.setCellsResizable(false);
			} finally {
				// End the transaction
				graph.getModel().endUpdate();
			}

			// Create a new stylesheet for the graph
			mxStylesheet stylesheet = graph.getStylesheet();

			// Get the default edge style
			Map<String, Object> edgeStyle = stylesheet.getDefaultEdgeStyle();

			// Customize the edge style properties
			edgeStyle.put(mxConstants.STYLE_STROKECOLOR, toHex(this.connectionLineColor)); // Edge color
			edgeStyle.put(mxConstants.STYLE_STROKEWIDTH, 0.5); // Line width
			edgeStyle.put(mxConstants.STYLE_DASHED, true); // Dashed line
			edgeStyle.put(mxConstants.STYLE_ENDARROW, mxConstants.NONE); // No arrow

			// Create a Swing component for the graph
			mxGraphComponent graphComponent = new mxGraphComponent(graph);
			graphComponent.setConnectable(false);
			graphComponent.setToolTips(true);
			graphComponent.getViewport().setBackground(Color.WHITE);

			JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout());
			panel.setBackground(Color.WHITE);
			JLabel label = new JLabel(title);
			label.setHorizontalAlignment(SwingConstants.CENTER);
			panel.add(label, BorderLayout.NORTH);
			panel.add(graphComponent, BorderLayout.CENTER);
			return panel;
		}

	}

	private class BarChartPlot {

		private ChartPanel createBarChartPlot(ExecResultData.Connection[] data, String title, String xAxisLabel,
				String yAxisLabel) {
			// Create dataset
			DefaultCategoryDataset dataset = new DefaultCategoryDataset();

			for (ExecResultData.Connection conn : data) {
				dataset.addValue(conn.value(), title, conn.id());
			}

			// Create chart
			JFreeChart chart = ChartFactory.createBarChart(
					title,
					xAxisLabel,
					yAxisLabel,
					dataset,
					PlotOrientation.VERTICAL,
					false, true, false);

			// Change the bar colors
			CategoryPlot plot = chart.getCategoryPlot();
			BarRenderer renderer = (BarRenderer) plot.getRenderer();
			// Get all series
			for (int i = 0; i < dataset.getRowCount(); i++) {
				// Get all categories
				for (int j = 0; j < dataset.getColumnCount(); j++) {
					renderer.setSeriesPaint(i, new Color((int) (Math.random() * 0x1000000)));
				}
			}

			plot.setRenderer(renderer);

			// Create Panel
			return new ChartPanel(chart);
		}

	}

	private class LexicalTree {

		private Color connectionLineColor;
		private Color nodesColor;

		public LexicalTree(Color connectionLineColor, Color nodesColor) {
			this.connectionLineColor = connectionLineColor;
			this.nodesColor = nodesColor;
		}

		public JPanel createTree(ExecResultDataTreeNode root, String title) {
			JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout());
			JLabel label = new JLabel(title);
			label.setBackground(Color.WHITE);
			label.setHorizontalAlignment(SwingConstants.CENTER);
			panel.add(label, BorderLayout.NORTH);

			mxGraph graph = new mxGraph();
			Object parent = graph.getDefaultParent();

			graph.getModel().beginUpdate();
			try {
				buildTreeFromRoot(graph, parent, null, root);
			} finally {
				graph.getModel().endUpdate();
			}
			// Create a new stylesheet for the graph
			mxStylesheet stylesheet = graph.getStylesheet();

			// Get the default edge style
			Map<String, Object> edgeStyle = stylesheet.getDefaultEdgeStyle();

			// Customize the edge style properties
			edgeStyle.put(mxConstants.STYLE_STROKECOLOR, toHex(this.connectionLineColor)); // Edge color
			edgeStyle.put(mxConstants.STYLE_STROKEWIDTH, 1); // Line width
			edgeStyle.put(mxConstants.STYLE_ENDARROW, mxConstants.NONE); // No arrow

			// Apply tree layout
			mxCompactTreeLayout layout = new mxCompactTreeLayout(graph);
			layout.execute(parent);

			mxGraphComponent graphComponent = new mxGraphComponent(graph);
			graphComponent.setConnectable(false);
			graphComponent.setToolTips(true);
			graphComponent.getViewport().setBackground(Color.WHITE);

			panel.add(graphComponent, BorderLayout.CENTER);

			return panel;
		}

		private void buildTreeFromRoot(mxGraph graph, Object parent, Object previousNode,
				ExecResultDataTreeNode currentNode) {

			Object vertex = graph.insertVertex(parent, null, currentNode.id(), 10, 10, 80, 30,
					"fillColor=" + toHex(this.nodesColor));

			if (previousNode != null) {
				graph.insertEdge(parent, null, "", previousNode, vertex);
			}
			if (currentNode.children() != null) {
				for (ExecResultDataTreeNode child : currentNode.children()) {
					buildTreeFromRoot(graph, parent, vertex, child);
				}
			}
		}

	}

}
