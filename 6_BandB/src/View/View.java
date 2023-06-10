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
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.SpinnerNumberModel;

import Model.Board;
import Model.Heuristic;
import Model.Movement;
import Model.Solution;

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

	private JSpinner[] spinners;

	private Board lastBoard;

	private PuzzleUI pUI;

	private Heuristic selectedHeuristic;

	private String selectedImg;
	private JLabel loadingFeedback;
	private JLabel loadingLabel;

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
			case CALCULATE -> {
				final Solution sol = (Solution) request.body.content;
				for (Movement move : sol.movements()) {
					this.pUI.removeAll();
					this.lastBoard.move(move);
					this.pUI.changeBoardState(this.lastBoard.getState());
					this.pUI.setImage(this.selectedImg, this.lastBoard.getState().length);
					this.pUI.paintComponent(this.pUI.getGraphics());
					this.pUI.validate();
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				}
				this.loadingLabel.setText("");
				this.loadingFeedback.setIcon(null);
			}
			case FETCH_STATS -> {
				final Solution[] sol = (Solution[]) request.body.content;
				WindowStats stats = new WindowStats(sol);
				stats.show();
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

		JPanel heuristicPanel = new JPanel();
		heuristicPanel.setLayout(new BoxLayout(heuristicPanel, BoxLayout.Y_AXIS));
		heuristicPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		heuristicPanel.setBackground(Color.WHITE);
		JLabel heuristicLabel = new JLabel("Heurística");
		heuristicLabel.setBackground(Color.WHITE);
		heuristicLabel.setOpaque(true);
		final String fontName = "Arial";
		heuristicLabel.setFont(new Font(fontName, Font.ITALIC, 14));
		heuristicLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		JComboBox<String> heuristic = new JComboBox<>();

		this.selectedHeuristic = Heuristic.BAD_POSITION;
		for (Heuristic heuristic2 : Heuristic.values()) {
			heuristic.addItem(heuristic2.name());
		}
		heuristic.setSelectedItem(this.selectedHeuristic.name());
		heuristic.addActionListener(
				e -> this.selectedHeuristic = Heuristic.valueOf((String) heuristic.getSelectedItem()));
		heuristicPanel.add(heuristicLabel);
		heuristicPanel.add(heuristic);
		// The max size to the solveStrategyLabel size
		heuristicPanel.setMaximumSize(new Dimension(
				(int) heuristicPanel.getPreferredSize().getWidth() + 40,
				(int) heuristicPanel.getPreferredSize().getHeight()));

		JPanel imgPanel = new JPanel();
		imgPanel.setLayout(new BoxLayout(imgPanel, BoxLayout.Y_AXIS));
		imgPanel.setBackground(Color.WHITE);
		JLabel imgLabel = new JLabel("Selecciona una imagen");
		imgLabel.setBackground(Color.WHITE);
		imgLabel.setFont(new Font(fontName, Font.ITALIC, 14));
		imgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		JComboBox<String> imgBox = new JComboBox<>();
		imgBox.addItem("Nada");
		imgBox.addItem("Pájaro");
		imgBox.addItem("Gato");
		imgBox.addItem("Perro");
		this.selectedImg = null;
		imgBox.setSelectedItem("Nada");
		imgBox.addActionListener(e -> {
			this.selectedImg = switch ((String) imgBox.getSelectedItem()) {
				case "Nada" -> null;
				case "Pájaro" -> "bird.jpg";
				case "Gato" -> "cat.jpg";
				case "Perro" -> "dog.jpg";
				default -> null;
			};
			this.pUI.removeAll();
			if (Objects.nonNull(this.selectedImg)) {
				this.pUI.setImage(this.selectedImg, this.lastBoard.getState().length);
			}
			this.pUI.paintComponent(this.pUI.getGraphics());
			this.pUI.validate();
		});
		imgPanel.add(imgLabel);
		imgPanel.add(imgBox);

		// The max size to the solveStrategyLabel size
		imgPanel.setMaximumSize(new Dimension(
				(int) imgPanel.getPreferredSize().getWidth() + 40,
				(int) imgPanel.getPreferredSize().getHeight()));

		// Set the same start location for both panels
		solveStrategyPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		heuristicPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		imgPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

		actionsPanel.add(Box.createVerticalStrut(5));
		actionsPanel.add(solveStrategyPanel);
		actionsPanel.add(Box.createVerticalStrut(15));
		actionsPanel.add(heuristicPanel);
		actionsPanel.add(Box.createVerticalStrut(5));
		actionsPanel.add(imgPanel);
		actionsPanel.add(Box.createVerticalStrut(5));

		sideBar.add(actionsPanel);

		// Add size to the puzzle
		JPanel puzzleSizePanel = new JPanel();
		puzzleSizePanel.setBackground(Color.WHITE);
		puzzleSizePanel.setLayout(new BoxLayout(puzzleSizePanel, BoxLayout.Y_AXIS));
		JLabel puzzleSizeLabel = new JLabel("Tamaño del puzzle");
		// Crear un modelo para el JSpinner con un rango de valores válidos
		int boardSize = 4;
		SpinnerNumberModel spinnerModel = new SpinnerNumberModel(boardSize, 2,10, 1);
		this.lastBoard = new Board(boardSize);

		// Crear el JSpinner utilizando el modelo
		JSpinner puzzleSize = new JSpinner(spinnerModel);

		// Personalizar la apariencia del JSpinner
		puzzleSize.setFont(new Font(fontName, Font.PLAIN, 14));

		// Change the size of the puzzle to be the same as the label size
		puzzleSize.setMaximumSize(new Dimension(
				(int) puzzleSizeLabel.getPreferredSize().getWidth() + 20,
				(int) puzzleSizeLabel.getPreferredSize().getHeight() + 15));

		// Agregar un listener para detectar cambios en el valor del JSpinner
		puzzleSize.addChangeListener(e -> {
			this.lastBoard = new Board((int) puzzleSize.getValue());
			this.pUI.removeAll();
			this.pUI.changeBoardState(this.lastBoard.getState());
			this.pUI.setImage(this.selectedImg, this.lastBoard.getState().length);
			this.pUI.paintComponent(this.pUI.getGraphics());
			this.pUI.validate();
		});

		// Put the same start location for both components
		puzzleSizeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		puzzleSize.setAlignmentX(Component.CENTER_ALIGNMENT);

		JPanel loadingPanel = new JPanel();
		loadingPanel.setBackground(Color.WHITE);
		this.loadingLabel = new JLabel("");
		this.loadingFeedback = new JLabel();
		loadingPanel.add(this.loadingLabel);
		loadingPanel.add(this.loadingFeedback);

		// Agregar el JSpinner al panel puzzleSizePanel
		puzzleSizePanel.add(puzzleSizeLabel);
		puzzleSizePanel.add(puzzleSize);
		puzzleSizePanel.add(Box.createVerticalStrut(20));
		puzzleSizePanel.add(loadingPanel);

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

		this.pUI = new PuzzleUI(this.lastBoard.getState());
		content.add(pUI, BorderLayout.CENTER);
		splitPane.setLeftComponent(content);
		Section body = new Section();
		body.createJSplitPaneSection(splitPane);
		return body;
	}

	private Section footer() {
		Section footer = new Section();
		this.buttons = new JButton[2];
		this.spinners = new JSpinner[2];

		JLabel shuffleAmount = new JLabel("Pasos");
		this.spinners[0] = new JSpinner(new SpinnerNumberModel(100, 1, 200, 1));

		JLabel seed = new JLabel("Semilla");
		this.spinners[1] = new JSpinner(new SpinnerNumberModel(0, Integer.MIN_VALUE, Integer.MAX_VALUE, 1));

		this.buttons[0] = new JButton("Barajar");
		this.buttons[0].addActionListener(e -> {
			this.lastBoard.shuffle((int) this.spinners[0].getValue(), (int) this.spinners[1].getValue());
			this.pUI.removeAll();
			this.pUI.changeBoardState(this.lastBoard.getState());
			this.pUI.setImage(this.selectedImg, this.lastBoard.getState().length);
			this.pUI.paintComponent(this.pUI.getGraphics());
			this.pUI.validate();
		});
		this.buttons[1] = new JButton("Resolver");
		this.buttons[1].addActionListener(e -> {
			final Body body = new Body(new Object[] { this.lastBoard, this.selectedHeuristic });
			this.sendRequest(new Request(RequestCode.CALCULATE, this, body));
			this.loadingLabel.setText("Calculando...");
			ImageIcon loading = new ImageIcon(Config.PATH_TO_LOADING_ASSET);
			loading.setImage(loading.getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT));
			this.loadingFeedback.setIcon(loading);
		});
		Section butons = new Section();
		butons.createButtons(buttons, DirectionAndPosition.DIRECTION_ROW);

		JPanel footerPanel = new JPanel();

		footerPanel.add(shuffleAmount);
		footerPanel.add(this.spinners[0]);
		footerPanel.add(seed);
		footerPanel.add(this.spinners[1]);
		footerPanel.add(butons.getPanel());

		footer.createFreeSection(footerPanel);
		return footer;
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
		alg.addActionListener(e -> this.sendRequest(new Request(RequestCode.FETCH_STATS, this)));
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
		private BufferedImage[][] images;

		public PuzzleUI(int[][] puzzle) {
			this.puzzle = puzzle;
			this.pWidth = puzzle.length;
			this.pHeight = puzzle[0].length;
			this.images = new BufferedImage[this.pWidth][this.pHeight];
			this.setLayout(new BorderLayout());
		}

		public void changeBoardState(int[][] puzzle) {
			this.puzzle = puzzle;
			this.pWidth = puzzle.length;
			this.pHeight = puzzle[0].length;
			this.images = new BufferedImage[this.pWidth][this.pHeight];
		}

		public void setImage(String imgName, int n) {
			if (Objects.isNull(imgName)) {
				return;
			}
			final String path = Config.IMAGE_PATH + imgName;
			try {
				// Load the original image
				BufferedImage originalImage = ImageIO.read(new File(path));

				// Calculate the width and height of each small image
				int width = originalImage.getWidth() / n;
				int height = originalImage.getHeight() / n;

				// Split the image into smaller images
				for (int i = 0; i < n; i++) {
					for (int j = 0; j < n; j++) {
						// Create a sub-image of the original image
						this.images[i][j] = originalImage.getSubimage(j * width, i * height, width, height);
					}
				}
				Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Image loaded: {0}, with size: {1}x{2}",
						new Object[] { imgName, width, height });
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private BufferedImage getImage(int value) {
			int row = (value - 1) / this.pWidth;
			int col = (value - 1) % this.pHeight;
			return this.images[row][col];
		}

		@Override
		public void paintComponent(Graphics g) {
			JPanel panel = new JPanel();
			panel.setLayout(new GridLayout(this.pWidth, this.pHeight));
			panel.setBackground(Color.WHITE);
			for (int row = 0; row < this.pWidth; row++) {
				for (int col = 0; col < this.pHeight; col++) {
					if (puzzle[row][col] != -1) {
						if (Objects.nonNull(View.this.selectedImg)) {
							ImageFillButton button = new ImageFillButton(
									new ImageIcon(this.getImage(this.puzzle[row][col])));
							button.setBackground(Color.WHITE);
							button.setBorder(BorderFactory.createLineBorder(Color.WHITE));
							panel.add(button);
						} else {
							JButton button = new JButton(String.valueOf(this.puzzle[row][col]));
							button.setBackground(Color.WHITE);
							button.setBorder(BorderFactory.createLineBorder(Color.WHITE));
							panel.add(button);
						}
					} else {
						panel.add(new JLabel(""));
					}
				}
			}
			this.add(panel, BorderLayout.CENTER);
		}
	}

	private class ImageFillButton extends JButton {
		private Image image;

		public ImageFillButton(ImageIcon icon) {
			image = icon.getImage();
			setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (image != null) {
				g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
			}
		}
	}

}
