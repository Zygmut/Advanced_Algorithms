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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
	 * This constructor creates a view with the MVC hub without any configuration
	 *
	 * @param mvc The MVC hub of the view.
	 * @see MVC
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
	 * @see MVC
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
			case GREET -> {
				Logger.getLogger(this.getClass().getSimpleName())
						.log(Level.INFO, "Hi {0}!.", request.origin);
				this.textArea.append("Hi " + request.origin + "!\n");
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

		JPanel solveStrategyPanel = new JPanel();
		solveStrategyPanel.setBackground(Color.WHITE);
		solveStrategyPanel.setLayout(new BoxLayout(solveStrategyPanel, BoxLayout.Y_AXIS));
		solveStrategyPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		JLabel solveStrategyLabel = new JLabel("Estrategia de resolución");
		solveStrategyLabel.setFont(new Font("Arial", Font.ITALIC, 14));
		solveStrategyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		JCheckBox random = new JCheckBox("Aleatoria");
		random.setSelected(true);
		random.addActionListener(e -> {
			// TODO
		});
		JCheckBox bfs = new JCheckBox("BFS");
		bfs.addActionListener(e -> {
			// TODO
		});
		JCheckBox dfs = new JCheckBox("DFS");
		dfs.addActionListener(e -> {
			// TODO
		});
		JCheckBox aStar = new JCheckBox("A*");
		aStar.addActionListener(e -> {
			// TODO
		});
		ButtonGroup group = new ButtonGroup();
		group.add(random);
		group.add(bfs);
		group.add(dfs);
		group.add(aStar);

		solveStrategyPanel.add(solveStrategyLabel);
		solveStrategyPanel.add(random);
		solveStrategyPanel.add(bfs);
		solveStrategyPanel.add(dfs);
		solveStrategyPanel.add(aStar);

		JPanel heuristicPanel = new JPanel();
		heuristicPanel.setLayout(new BoxLayout(heuristicPanel, BoxLayout.Y_AXIS));
		heuristicPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		heuristicPanel.setBackground(Color.WHITE);
		JLabel heuristicLabel = new JLabel("Heurística");
		heuristicLabel.setBackground(Color.WHITE);
		heuristicLabel.setOpaque(true);
		heuristicLabel.setFont(new Font("Arial", Font.ITALIC, 14));
		heuristicLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		JComboBox<String> heuristic = new JComboBox<>();
		heuristic.addItem("Manhattan");
		heuristic.addItem("Euclídea");
		heuristic.addItem("Hamming");
		heuristic.addActionListener(e -> {
			// TODO
		});
		heuristicPanel.add(heuristicLabel);
		heuristicPanel.add(heuristic);
		// The max size to the solveStrategyLabel size
		heuristicPanel.setMaximumSize(new Dimension(
				(int) solveStrategyLabel.getPreferredSize().getWidth(),
				(int) heuristicPanel.getPreferredSize().getHeight()));

		// Set the same start location for both panels
		solveStrategyPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		heuristicPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

		actionsPanel.add(Box.createVerticalStrut(5));
		actionsPanel.add(solveStrategyPanel);
		actionsPanel.add(Box.createVerticalStrut(5));
		actionsPanel.add(heuristicPanel);
		actionsPanel.add(Box.createVerticalStrut(5));

		sideBar.add(actionsPanel);

		// Add size to the puzzle
		JPanel puzzleSizePanel = new JPanel();
		puzzleSizePanel.setBackground(Color.WHITE);
		puzzleSizePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel puzzleSizeLabel = new JLabel("Tamaño del puzzle");
		// Crear un modelo para el JSpinner con un rango de valores válidos
		SpinnerNumberModel spinnerModel = new SpinnerNumberModel(3, 2, 10, 1); // Valor inicial: 3, Mínimo: 2, Máximo:
																				// 10, Incremento: 1

		// Crear el JSpinner utilizando el modelo
		JSpinner puzzleSize = new JSpinner(spinnerModel);

		// Personalizar la apariencia del JSpinner
		puzzleSize.setPreferredSize(new Dimension(100, 30));
		puzzleSize.setFont(new Font("Arial", Font.PLAIN, 14));

		// Agregar un listener para detectar cambios en el valor del JSpinner
		puzzleSize.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				int newSize = (int) puzzleSize.getValue();
				//TODO: Change the size of the puzzle
			}
		});

		// Agregar el JSpinner al panel puzzleSizePanel
		puzzleSizePanel.add(puzzleSizeLabel, BorderLayout.NORTH);
		puzzleSizePanel.add(puzzleSize, BorderLayout.CENTER);
		sideBar.add(puzzleSizePanel);
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
		JPanel content = new JPanel();
		content.setLayout(new BorderLayout());
		content.setBackground(Color.WHITE);
		// Color[] colors = { Color.BLACK, Color.BLUE, Color.GREEN, Color.ORANGE,
		// Color.RED, Color.WHITE };
		// DrawRubikCube drawRubikCube = new DrawRubikCube(600, 600, colors);
		// content.add(drawRubikCube, BorderLayout.CENTER);
		Game15Puzzle game15Puzzle = new Game15Puzzle(600, 600, 4);
		content.add(game15Puzzle, BorderLayout.CENTER);
		splitPane.setLeftComponent(content);
		Section body = new Section();
		body.createJSplitPaneSection(splitPane);
		return body;
	}

	private Section footer() {
		Section buttonSection = new Section();
		this.buttons = new JButton[2];

		this.buttons[0] = new JButton("Generar cubo aleatorio");
		this.buttons[0].addActionListener(e -> {
			// TODO

		});
		this.buttons[1] = new JButton("Resolver");
		this.buttons[1].addActionListener(e -> {
			Request request = new Request(RequestCode.GREET, this, new Body("Anybody there?!"));
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

		JMenu db = new JMenu("Datos");
		JMenuItem load = new JMenuItem("Cargar");
		load.addActionListener(e -> {
			Request request = new Request(RequestCode.CREATE_DB, this);
			this.sendRequest(request);
		});
		db.add(load);
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

	private class Game15Puzzle extends JPanel {
		private int width;
		private int height;
		private int size;
		private int[][] puzzle;

		public Game15Puzzle(int width, int height, int size) {
			this.width = width;
			this.height = height;
			this.size = size;
			this.puzzle = new int[size][size];
			this.initializePuzzle();
		}

		@Override
		public void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;

			int cellWidth = width / size;
			int cellHeight = height / size;
			Color backgroundColor = new Color(240, 240, 240); // Color de fondo de las celdas
			Color borderColor = new Color(160, 160, 160); // Color del borde de las celdas
			Color textColor = new Color(50, 50, 50); // Color del texto en las celdas

			for (int row = 0; row < size; row++) {
				for (int col = 0; col < size; col++) {

					int value = puzzle[row][col];
					int x = col * cellWidth;
					int y = row * cellHeight;
					g2.setColor(backgroundColor);
					g2.fillRect(x, y, cellWidth, cellHeight);
					g2.setColor(borderColor);
					g2.drawRect(x, y, cellWidth, cellHeight);
					g2.setFont(getFont());
					g2.setFont(new Font("Arial", Font.BOLD, 24));
					g2.setColor(textColor);
					FontMetrics fm = g2.getFontMetrics();
					int textWidth = fm.stringWidth(String.valueOf(value));
					int textHeight = fm.getHeight();
					int textX = x + (cellWidth - textWidth) / 2;
					int textY = y + (cellHeight + textHeight) / 2;
					if (value != 0) {
						g2.drawString(String.valueOf(value), textX, textY);
					}

				}
			}
		}

		private void initializePuzzle() {
			int count = 1;
			for (int row = 0; row < size; row++) {
				for (int col = 0; col < size; col++) {
					puzzle[row][col] = count;
					count++;
				}
			}
			puzzle[size - 1][size - 1] = 0; // Empty cell
		}
	}

	private class DrawRubikCube extends JPanel {

		private int width;
		private int height;
		private int size;
		private Color[] cube;

		public DrawRubikCube(int width, int height, Color[] cube) {
			this.width = width;
			this.height = height;
			this.cube = cube;
			this.size = width * height * 6;
		}

		@Override
		public void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			int x = 0;
			int y = 0;
			int faceWidth = width / 3;
			int faceHeight = height / 3;
			for (int i = 0; i < 6; i++) {
				drawCube(g2, x, y, faceWidth, faceHeight, cube);
				x += faceWidth;
			}
		}

		private void drawFace(Graphics2D g2, int x, int y, int width, int height, Color color) {
			g2.setColor(color);
			g2.fillRect(x, y, width, height);
			g2.setColor(Color.BLACK);
			g2.drawRect(x, y, width, height);
		}

		private void drawCube(Graphics2D g2, int x, int y, int width, int height, Color[] cube) {
			int faceWidth = width / 3;
			int faceHeight = height / 3;
			int faceX = x + faceWidth;
			int faceY = y + faceHeight;

			// Front face
			drawFace(g2, faceX, faceY, faceWidth, faceHeight, cube[0]);
			// Top face
			drawFace(g2, faceX, faceY - faceHeight, faceWidth, faceHeight, cube[1]);
			// Right face
			drawFace(g2, faceX + faceWidth, faceY, faceWidth, faceHeight, cube[2]);
			// Bottom face
			drawFace(g2, faceX, faceY + faceHeight, faceWidth, faceHeight, cube[3]);
			// Left face
			drawFace(g2, faceX - faceWidth, faceY, faceWidth, faceHeight, cube[4]);
			// Back face
			drawFace(g2, faceX + faceWidth, faceY + faceHeight, faceWidth, faceHeight, cube[5]);
		}

	}

}
