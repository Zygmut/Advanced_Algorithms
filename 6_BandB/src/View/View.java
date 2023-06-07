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
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.SpinnerNumberModel;

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
		heuristicLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
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
		solveStrategyPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		heuristicPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

		actionsPanel.add(Box.createVerticalStrut(5));
		actionsPanel.add(solveStrategyPanel);
		actionsPanel.add(Box.createVerticalStrut(5));
		actionsPanel.add(heuristicPanel);
		actionsPanel.add(Box.createVerticalStrut(5));

		sideBar.add(actionsPanel);

		// Add size to the puzzle
		JPanel puzzleSizePanel = new JPanel();
		puzzleSizePanel.setBackground(Color.WHITE);
		puzzleSizePanel.setLayout(new BoxLayout(puzzleSizePanel, BoxLayout.Y_AXIS));
		JLabel puzzleSizeLabel = new JLabel("Tamaño del puzzle");
		// Crear un modelo para el JSpinner con un rango de valores válidos
		SpinnerNumberModel spinnerModel = new SpinnerNumberModel(3, 2, 10, 1);

		// Crear el JSpinner utilizando el modelo
		JSpinner puzzleSize = new JSpinner(spinnerModel);

		// Personalizar la apariencia del JSpinner
		puzzleSize.setFont(new Font("Arial", Font.PLAIN, 14));

		// Change the size of the puzzle to be the same as the label size
		puzzleSize.setMaximumSize(new Dimension(
				(int) puzzleSizeLabel.getPreferredSize().getWidth(),
				(int) puzzleSizeLabel.getPreferredSize().getHeight() + 15));

		// Agregar un listener para detectar cambios en el valor del JSpinner
		puzzleSize.addChangeListener(e -> {
			// TODO
		});

		// Put the same start location for both components
		puzzleSizeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		puzzleSize.setAlignmentX(Component.CENTER_ALIGNMENT);

		puzzleSizePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

		// Agregar el JSpinner al panel puzzleSizePanel
		puzzleSizePanel.add(puzzleSizeLabel);
		puzzleSizePanel.add(puzzleSize);
		sideBar.add(puzzleSizePanel);

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

		// TODO: BORRAR ESTO - SOLO PARA PRUEBAS
		int size = 4;
		int[][] puzzle = new int[size][size];
		int count = 1;
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				puzzle[row][col] = count;
				count++;
			}
		}
		puzzle[size - 1][size - 1] = -1; // Empty cell
		// TODO: BORRAR ESTO - SOLO PARA PRUEBAS

		PuzzleUI pUI = new PuzzleUI(puzzle);
		content.add(pUI, BorderLayout.CENTER);
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

	private class PuzzleUI extends JPanel {
		private int pWidth;
		private int pHeight;
		private int[][] puzzle;

		public PuzzleUI(int[][] puzzle) {
			this.pWidth = puzzle.length;
			this.pHeight = puzzle[0].length;
			this.puzzle = puzzle;
			this.setLayout(new BorderLayout());
		}

		@Override
		public void paintComponent(Graphics g) {
			JPanel panelAux = new JPanel();
			panelAux.setLayout(new GridLayout(this.pWidth, this.pHeight));
			panelAux.setBackground(Color.WHITE);
			for (int row = 0; row < this.pWidth; row++) {
				for (int col = 0; col < this.pHeight; col++) {
					if (this.puzzle[row][col] != -1) {
						JButton button = new JButton(String.valueOf(this.puzzle[row][col]));
						button.setBackground(Color.WHITE);
						button.setBorder(BorderFactory.createLineBorder(Color.WHITE));
						button.addActionListener(e -> {
							// TODO
						});
						panelAux.add(button);
					} else {
						panelAux.add(new JLabel(""));
					}
				}
			}
			this.add(panelAux, BorderLayout.CENTER);
		}

	}

}
